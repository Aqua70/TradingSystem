package backend.tradesystem.general_managers;


import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.Manager;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * Used for logging in and registering, as well as any setup like changing global settings
 */
public class LoginManager extends Manager {

    private final String TRADER_PROPERTY_FILE_PATH;

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public LoginManager() throws IOException {
        super();
        TRADER_PROPERTY_FILE_PATH = DatabaseFilePaths.TRADER_CONFIG.getFilePath();
    }


    /**
     * Registers a user
     *
     * @param username username of new user
     * @param password password of new user
     * @param type     the type of user
     * @return The ID of the newly created user
     * @throws UserAlreadyExistsException username is not unique
     * @throws BadPasswordException       password isn't valid
     * @throws IOException                issues with getting database files
     */
    public String registerUser(String username, String password, UserTypes type) throws UserAlreadyExistsException, BadPasswordException, IOException {


        validatePassword(password);

        if (!isUsernameUnique(username))
            throw new UserAlreadyExistsException();
        switch (type) {
            case ADMIN:
                Admin admin = new Admin(username, password);
                for (String userId : getAllUsers()) {
                    try {
                        if (getUser(userId) instanceof Admin) {
                            admin.setReports(((Admin) getUser(userId)).getReports());
                            break;
                        }
                    } catch (UserNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return updateUserDatabase(admin).getId();
            case TRADER:
                tryToRefreshTradeCount();
            default:
                // Get current default limits
                int defaultTradeLimit = getProperty(TraderProperties.TRADE_LIMIT);
                int defaultIncompleteTradeLim = getProperty(TraderProperties.INCOMPLETE_TRADE_LIM);
                int defaultMinimumAmountNeededToBorrow = getProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW);
                return updateUserDatabase(new Trader(username, password, "", defaultTradeLimit, defaultIncompleteTradeLim,
                        defaultMinimumAmountNeededToBorrow)).getId();
        }
    }



    /**
     * Ensuring user credentials is correct and returns the user id
     *
     * @param username username of user
     * @param password password of user
     * @return the user id of the logged in user
     * @throws UserNotFoundException could not find the user
     */
    public String login(String username, String password) throws UserNotFoundException {
        String userId = getUserByUsername(username);
        User user = getUser(userId);
        if (!user.getPassword().equals(password)) throw new UserNotFoundException();
        if (getType(user.getId()).equals(UserTypes.TRADER)) {
            try {
                tryToRefreshTradeCount();
            } catch (IOException ex) {
                System.out.println("Couldn't reset trade limits");
            }
        }
        return user.getId();
    }



    /**
     * Checks if the username exists in the database file
     *
     * @param username username to check for
     * @return if username is unique
     */
    public boolean isUsernameUnique(String username) {
        try {
            getUserByUsername(username);
            return false;
        } catch (UserNotFoundException ignored) {
            return true;
        }
    }


    /**
     * Get the type of user
     *
     * @param userId the user id
     * @return the user type of the user
     * @throws UserNotFoundException if the user id wasn't found
     */
    public UserTypes getType(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        if (user instanceof Admin)
            return UserTypes.ADMIN;
        else {
            return UserTypes.TRADER;
        }

    }
    /**
     * Checks if password is valid
     *
     * @param password must have no white space, length greater than 11, has a capital letter, has a number
     * @throws BadPasswordException if the password is not valid
     */
    private void validatePassword(String password) throws BadPasswordException {
        if (password.contains(" ")) throw new BadPasswordException("No white space allowed");
        if (password.length() < 11) throw new BadPasswordException("Length of password must be at least 12");
        if (password.toLowerCase().equals(password))
            throw new BadPasswordException("Must have at least one capital letter");
        if (!password.matches(".*[0-9]+.*")) throw new BadPasswordException("Must contain at least one number");
    }


    /**
     * Tries to refresh the trade count of all traders (this only happens every week).
     */
    private void tryToRefreshTradeCount() throws IOException {
        Date date = new Date();

        // Gets the current time in weeks since 1970.
        int currTime = (int) (date.getTime() / (1000 * 60 * 60 * 24 * 7));

        // Gets the last time (in weeks) the trade count of every user has been updated
        int lastTime = getProperty(TraderProperties.LAST_TRADE_COUNT_UPDATE);

        // If the time is different (i.e. one week has passed)...
        if (lastTime - currTime != 0) {
            // Refresh the trade count
            refreshTradeCount();

            // Set the new date that the trade count was updated to currTime.
            setProperty(TraderProperties.LAST_TRADE_COUNT_UPDATE, currTime);
        }
    }

    /**
     * Gets the current value of the specified trader property
     *
     * @param propertyType the type of property
     * @return the value of the specified trader property
     * @throws IOException if the input file doesn't exist
     */
    public int getProperty(TraderProperties propertyType) throws IOException {
        // get the file
        File propertyFile = new File(TRADER_PROPERTY_FILE_PATH);
        // initialize the reader of this file
        FileReader reader = new FileReader(propertyFile);
        // initialize properties object
        Properties properties = new Properties();
        // associate properties object with this file.
        properties.load(reader);
        // we're not going to use reader anymore, so close it
        reader.close();
        // return the integer value of that property
        return Integer.parseInt(properties.getProperty(propertyType.getProperty()));
    }

    /**
     * Sets the value of a property.
     *
     * @param propertyName  the property to change
     * @param propertyValue the new value of that property
     * @throws IOException if the file doesn't exist
     */
    public void setProperty(TraderProperties propertyName, int propertyValue) throws IOException {
        // get the file
        File propertyFile = new File(TRADER_PROPERTY_FILE_PATH);
        // initialize reader
        FileReader reader = new FileReader(propertyFile);
        // initialize properties object (to set data)
        Properties properties = new Properties();
        // associate this properties object with the file
        properties.load(reader);
        // set the property
        properties.setProperty(propertyName.getProperty(), "" + propertyValue);

        //update the file
        FileWriter writer = new FileWriter(propertyFile);
        properties.store(writer, "");
        reader.close();
        writer.close();
        updateAllTraderDefaults();
    }
    private void updateAllTraderDefaults() throws IOException {

        int defaultTradeLimit = getProperty(TraderProperties.TRADE_LIMIT);
        int defaultIncompleteTradeLim = getProperty(TraderProperties.INCOMPLETE_TRADE_LIM);
        int defaultMinimumAmountNeededToBorrow = getProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW);
        Set<String> users = getAllUsers();

        for (String user : users) {
            User populatedUser = null;
            try {
                populatedUser = getUser(user);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            if (populatedUser instanceof Trader) {
                Trader t = (Trader) populatedUser;
                t.setLimit(TraderProperties.TRADE_LIMIT, defaultTradeLimit);
                t.setLimit(TraderProperties.INCOMPLETE_TRADE_LIM, defaultIncompleteTradeLim);
                t.setLimit(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW, defaultMinimumAmountNeededToBorrow);
            }
            updateUserDatabase(populatedUser);
        }
    }

    /**
     * Refreshes the trade count of all the traders (sets the trade count to 0)
     */
    private void refreshTradeCount() {
        Set<String> users = getAllUsers();
        for (String id : users) {
            Trader trader = null;
            try {
                trader = getTrader(id);
                trader.setTradeCount(0);
            }
            catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            catch (AuthorizationException ignored){
                continue;
            }
            updateUserDatabase(trader);
        }
    }

}

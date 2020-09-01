package backend.tradesystem.trader_managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.Report;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.Manager;

import java.io.IOException;

/**
 * For changing existing settings for a trader
 */
public class SettingsManager extends Manager {


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public SettingsManager() throws IOException {
    }

    /**
     * Sets the city of the trader
     *
     * @param traderId the trader
     * @param city     the city
     * @return the trader id
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public String setCity(String traderId, String city) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        trader.setCity(city);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Set idle status, an idle trader has some limitations such as being unable to trade
     *
     * @param traderId the trader
     * @param status   whether the trader is idle
     * @return the trader id
     * @throws UserNotFoundException  if the trader isn't found
     * @throws AuthorizationException if unable to go idle
     */
    public String setIdle(String traderId, boolean status) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (status && trader.getAcceptedTrades().size() > 0)
            throw new AuthorizationException("Cannot go idle until ongoing trades have been resolved");
        trader.setIdle(status);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Change the username of an existing user
     *
     * @param userId   the existing user
     * @param username the new username
     * @return the user id
     * @throws UserAlreadyExistsException if the username is taken
     * @throws UserNotFoundException      the userId wasn't found
     */
    public String changeUsername(String userId, String username) throws UserAlreadyExistsException, UserNotFoundException {
        if (!isUsernameUnique(username)) {
            throw new UserAlreadyExistsException();
        }
        User user = getUser(userId);
        user.setUsername(username);
        updateUserDatabase(user);
        return user.getId();
    }

    /**
     * Change password of the user
     *
     * @param userId   the user
     * @param password the new password
     * @return the user id
     * @throws BadPasswordException  if the password isn't valid
     * @throws UserNotFoundException if the user wasn't found
     */
    public String changePassword(String userId, String password) throws BadPasswordException, UserNotFoundException {
        validatePassword(password);
        User user = getUser(userId);
        user.setPassword(password);
        updateUserDatabase(user);
        return user.getId();
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
     * Reporting a user
     *
     * @param fromUserId the user that sent the report
     * @param toUserId   user being reported
     * @param message    what the report is about
     * @return whether or not the report successfully went through
     * @throws UserNotFoundException  user wasn't found
     * @throws AuthorizationException report is invalid
     */
    public boolean reportUser(String fromUserId, String toUserId, String message) throws UserNotFoundException, AuthorizationException {
        boolean successful = false;
        if (fromUserId.equals(toUserId)) throw new AuthorizationException("You cannot report yourself.");
        if (getUser(fromUserId).isFrozen())
            throw new AuthorizationException("This user is frozen and can't report others.");
        Report report = new Report(fromUserId, toUserId, message);

        // Add the report to all admins so that they can see the report.
        for (String userId : getAllUsers()) {
            if (getUser(userId) instanceof Admin) {
                Admin admin = ((Admin) getUser(userId));
                admin.getReports().add(report);
                updateUserDatabase(admin);
                successful = true;
            }
        }
        return successful;
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
}

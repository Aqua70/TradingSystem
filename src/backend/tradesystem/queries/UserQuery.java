package backend.tradesystem.queries;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.Review;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.Manager;
import backend.tradesystem.UserTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * For getting info about a specific user or users in general
 */
public class UserQuery extends Manager {
    /**
     * For getting access to database files
     *
     * @throws IOException issues with getting the file path
     */
    public UserQuery() throws IOException {
        super();
    }

    /**
     * Gets a given user's username
     *
     * @param userId The id of the user being checked
     * @return the user's username
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public String getUsername(String userId) throws UserNotFoundException {
        return getUser(userId).getUsername();
    }

    /**
     * Gets a given user's password
     *
     * @param userId The id of the user being checked
     * @return the user's password
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public String getPassword(String userId) throws UserNotFoundException {
        return getUser(userId).getPassword();
    }

    /**
     * Gets a given user's current frozen status
     *
     * @param userId The id of the user being checked
     * @return if the user is frozen
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public boolean isFrozen(String userId) throws UserNotFoundException {
        return getUser(userId).isFrozen();
    }

    /**
     * Return true if a given user requested to be unfrozen
     *
     * @param userId The id of the user being checked
     * @return if the user requested to be unfrozen
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public boolean isUnfrozenRequested(String userId) throws UserNotFoundException {
        return getUser(userId).isUnfrozenRequested();
    }


    /**
     * Gets all reviews of a given trader
     * It is returned in the form of [fromUserId, toUserId, message, rating, reportId] for each element in the list
     *
     * @param traderId The id of the trader being checked
     * @return all reviews of the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public List<String[]> getReviews(String traderId) throws UserNotFoundException, AuthorizationException {
        List<String[]> reviews = new ArrayList<>();
        for (Review review : getTrader(traderId).getReviews()) {
            String[] items = {review.getFromUserId(), review.getReportOnUserId(), review.getMessage(),
                    review.getRating() + "", review.getId()};
            reviews.add(items);
        }
        return reviews;
    }


    /**
     * Return the city of a given trader
     *
     * @param traderId The id of the trader being checked
     * @return city of the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public String getCity(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getCity();
    }

    /**
     * Return true if the given user is idle
     *
     * @param traderId The id of the trader being checked
     * @return if the user is idle
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public boolean isIdle(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).isIdle();
    }


    /**
     * Gets a trader's wishlist
     *
     * @param traderId The id of the trader being checked
     * @return the trader's wishlist
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */

    public List<String> getWishlist(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getWishlist();
    }

    /**
     * Return a list of available items a given trader has
     *
     * @param traderId The id of the trader being checked
     * @return list of available items the trader has
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */

    public List<String> getAvailableItems(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getAvailableItems();
    }


    /**
     * Return a list of items a given trader requested to borrow/trade
     *
     * @param traderId The id of the trader being checked
     * @return list of items the trader requested to borrow/trade
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public List<String> getRequestedItems(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getRequestedItems();
    }


    /**
     * Return a list of trades accepted by a given trader
     *
     * @param traderId The id of the trader being checked
     * @return list of trades accepted by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public List<String> getAcceptedTrades(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getAcceptedTrades();
    }


    /**
     * Return a list of trades requested by a given trader
     *
     * @param traderId The id of the trader being checked
     * @return list of trades requested by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public List<String> getRequestedTrades(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getRequestedTrades();
    }


    /**
     * Return the total number of items borrowed by a given trader
     *
     * @param traderId The id of the trader being checked
     * @return total number of items borrowed by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getTotalItemsBorrowed(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getTotalItemsBorrowed();
    }

    /**
     * Return the total number of items lent by a given trader
     *
     * @param traderId The id of the trader being checked
     * @return total number of items lent by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getTotalItemsLent(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getTotalItemsLent();
    }

    /**
     * Gets all the trader ids in the database
     *
     * @return all the traders in the database
     */
    public List<String> getAllTraders() {
        List<String> allTraders = new ArrayList<>();
        for (String userId : getAllUsers()) {
            try {
                getTrader(userId); // Check if it is a trader
                allTraders.add(userId);
            } catch (UserNotFoundException | AuthorizationException ignored) {
            }
        }
        return allTraders;
    }

    /**
     * Gets the trader that has the tradable item id
     *
     * @param id the tradable item id
     * @return the trader id
     * @throws TradableItemNotFoundException if the item id is invalid
     */
    public String getTraderThatHasTradableItemId(String id) throws TradableItemNotFoundException {
        for (String userId : getAllUsers()) {
            try {
                if (getTrader(userId).getAvailableItems().contains(id) || getTrader(userId).getOngoingItems().contains(id)) {
                    return userId;
                }
            } catch (UserNotFoundException | AuthorizationException ignored) {
            }
        }
        throw new TradableItemNotFoundException();
    }

    /**
     * Gets all the trader ids within the same city
     *
     * @param city the city name
     * @return list of all traders within the same city
     */
    public List<String> getAllTradersInCity(String city) {
        List<String> allTraders = new ArrayList<>();
        for (String userId : getAllUsers()) {
            try {
                getTrader(userId); // Checks if this is a trader
                if (((Trader) getUser(userId)).getCity().equalsIgnoreCase(city))
                    allTraders.add(userId);
            } catch (AuthorizationException | UserNotFoundException ignored) {
            }
        }
        return allTraders;
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


}

package backend.tradesystem.trader_managers;


import backend.exceptions.AuthorizationException;
import backend.exceptions.EntryNotFoundException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.Review;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.List;

/**
 * Used for the actions of a Trader
 */
public class TraderManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TraderManager() throws IOException {
        super();
    }


    /**
     * Makes this user request an item
     *
     * @param id   trader id
     * @param name name of the item
     * @param desc description of the item
     * @return the id
     * @throws UserNotFoundException  if the user was not found
     * @throws AuthorizationException not allowed to request an item
     */
    public String addRequestItem(String id, String name, String desc) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(id);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        TradableItem item = new TradableItem(name, desc);
        trader.getRequestedItems().add(item.getId());
        updateTradableItemDatabase(item);
        updateUserDatabase(trader);
        return id;
    }




    /**
     * Adds an item to this trader's wishlist
     *
     * @param traderId the trader id
     * @param itemId   the tradable item id to be added
     * @return the traderId
     * @throws UserNotFoundException         if the trader with the given userId is not found
     * @throws AuthorizationException        not allowed to add to the wishlist
     * @throws TradableItemNotFoundException the item wasn't found
     */
    public String addToWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException,
            TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        getTradableItem(itemId);
        if (!trader.getWishlist().contains(itemId)) {
            trader.getWishlist().add(itemId);
            updateUserDatabase(trader);
        }
        return traderId;
    }

    /**
     * Remove item from trader's wishlist
     *
     * @param traderId the trader
     * @param itemId   the item being removed
     * @return the trader id
     * @throws UserNotFoundException  if the trader isn't found
     * @throws AuthorizationException frozen account or if the user can't do this action
     */
    public String removeFromWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        trader.getWishlist().remove(itemId);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Remove an item from the trader's inventory
     *
     * @param traderId the trader
     * @param itemId   the item being removed
     * @return the trader id
     * @throws UserNotFoundException  if the trader isn't found
     * @throws AuthorizationException frozen account or if the user can't do this action
     */
    public String removeFromInventory(String traderId, String itemId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        trader.getAvailableItems().remove(itemId);
        updateUserDatabase(trader);
        removeInvalidWishlistItems();
        removeInvalidRequests();
        return traderId;
    }





    /**
     * Adds a new review
     *
     * @param fromUser the user who sent the review
     * @param toUser   the user who received the review
     * @param rating   rating must be between 0 to 10, if greater or less than those bounds then it will assume those bounds
     * @param message  the message of the review
     * @return the new review id
     * @throws UserNotFoundException  if the user ids don't exist
     * @throws AuthorizationException if the users aren't traders
     */
    public String addReview(String fromUser, String toUser, double rating, String message) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(toUser);
        getTrader(fromUser); // Makes sure this trader exists
        if (rating < 0) rating = 0;
        else if (rating > 10) rating = 10;
        Review review = new Review(fromUser, toUser, rating, message);
        trader.addReview(review);
        updateUserDatabase(trader);
        return review.getId();
    }


    private void removeInvalidRequests() throws UserNotFoundException {
        // Removes invalid trades

        for (String id : getAllUsers()) {
            if (!(getUser(id) instanceof Trader)) {
                continue;
            }
            Trader trader = (Trader) getUser(id);
            try {
                for (int i = trader.getRequestedTrades().size() - 1; i >= 0; i--) {
                    String tradeID = trader.getRequestedTrades().get(i);
                    // Populate required variables.
                    Trade t = getTrade(tradeID);
                    Trader firstTrader = getTrader(t.getFirstUserId());
                    Trader secondTrader = getTrader(t.getSecondUserId());

                    // Figure out whether the trade is still valid.
                    boolean isValid = (t.getFirstUserOffer().equals("") || firstTrader.getAvailableItems().contains(t.getFirstUserOffer())) &&
                            (t.getSecondUserOffer().equals("") || secondTrader.getAvailableItems().contains(t.getSecondUserOffer()));

                    if (!isValid) {
                        firstTrader.getRequestedTrades().remove(i);
                        secondTrader.getRequestedTrades().remove(i);
                        deleteTrade(tradeID);
                        updateUserDatabase(firstTrader);
                        updateUserDatabase(secondTrader);
                    }
                }
            } catch (EntryNotFoundException | AuthorizationException e) {
                e.printStackTrace();
            }
        }

    }

    private void removeInvalidWishlistItems(){
        // Removes invalid items
        try {
            for (String userId : getAllUsers()) {
                User user = getUser(userId);
                if (!(user instanceof Trader)){
                    continue;
                }
                Trader someTrader = (Trader) user;
                for (int i = someTrader.getAvailableItems().size() - 1; i >= 0; i--) {
                    try {
                        getTradableItem(someTrader.getAvailableItems().get(i));
                    } catch (TradableItemNotFoundException ignored) {
                        someTrader.getAvailableItems().remove(i);
                    }
                }
                for (int i = someTrader.getWishlist().size() - 1; i >= 0; i--) {
                    try {
                        getTradableItem(someTrader.getWishlist().get(i));
                    } catch (TradableItemNotFoundException ignored) {
                        someTrader.getWishlist().remove(i);
                    }
                }
                updateUserDatabase(someTrader);
            }
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }
}

package backend.tradesystem.admin_managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * For handling new TradableItem + purchasable items requests
 */

public class HandleItemRequestsManager extends Manager {
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public HandleItemRequestsManager() throws IOException {
        super();
    }


    /**
     * Gets a hashmap of trader ids to an arraylist of their requested item ids
     *
     * @return a hashmap of trader ids to an arraylist of their requested item ids
     */
    public HashMap<String, List<String>> getAllItemRequests() {
        HashMap<String, List<String>> allItems = new HashMap<>();

        for (String userId : getAllUsers()) {
            try {
                // Get requested item IDs
                List<String> requestedItems = getTrader(userId).getRequestedItems();

                // Add the populated list to the result
                allItems.put(userId, requestedItems);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            } catch (AuthorizationException ignored) {
            }
        }
        return allItems;
    }

    /**
     * Accepts all item requests
     */
    public void acceptAllItemRequests() {
        HashMap<String, List<String>> allRequests = getAllItemRequests();
        for (String traderId : allRequests.keySet()) {
            for (String reqId : allRequests.get(traderId)) {
                try {
                    processItemRequest(traderId, reqId, true);
                } catch (TradableItemNotFoundException | AuthorizationException | UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Process the item request of a user
     *
     * @param traderID   ID of the trader
     * @param reqItemID  the requested item to be confirmed or rejected
     * @param isAccepted true if item is accepted, false if rejected
     * @throws TradableItemNotFoundException tradable item id isn't found
     * @throws AuthorizationException        if the user isn't a trader
     * @throws UserNotFoundException         trader isn't found
     */
    public void processItemRequest(String traderID, String reqItemID, boolean isAccepted) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        Trader trader = getTrader(traderID);
        List<String> itemIDs = trader.getRequestedItems();
        if (!itemIDs.contains(reqItemID)) throw new TradableItemNotFoundException(reqItemID);
        if (isAccepted) {
            trader.getAvailableItems().add(reqItemID);
        }
        trader.getRequestedItems().remove(reqItemID);
        updateUserDatabase(trader);
    }


}

package backend.tradesystem.suggestion_strategies;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *  A suggestion strategy which can be used to return suggestions that are exactly what each trader wants, according
 *  to their wishlist
 */
public class ExactWishlistSuggestion extends Manager implements SuggestionStrategy{


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public ExactWishlistSuggestion() throws IOException {
        super();
    }

    /**
     * Gets all the trader ids in the database
     *
     * @return all the traders in the database
     */
    private List<String> getAllTraders() {
        List<String> allTraders = new ArrayList<>();
        for (String userId : getAllUsers()) {
            try {
                if (getUser(userId) instanceof Trader)
                    allTraders.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return allTraders;
    }

    /**
     * Gets all the trader ids within the same city
     *
     * @param city the city name
     * @return list of all traders within the same city
     */
    private List<String> getAllTradersInCity(String city) {
        List<String> allTraders = new ArrayList<>();
        for (String userId : getAllTraders()) {
            try {
                if (getTrader(userId).getCity().equalsIgnoreCase(city))
                    allTraders.add(userId);
            } catch (UserNotFoundException | AuthorizationException e) {
                e.printStackTrace();
            }
        }
        return allTraders;
    }

    /**
     * Returns a list of the best lends that trader thisTraderId can preform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemId]
     *
     * @param thisTraderId The id of the trader that will be lending the item
     * @param inCity       Whether to only search for possible trades within the trader's city
     * @return a list of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    private List<String[]> suggestLendList(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");
        List<String[]> result = new ArrayList<>();
        HashSet<String> thisTraderItems = new HashSet<>(thisTrader.getAvailableItems());

        List<String> allTraders = inCity ? getAllTradersInCity(thisTrader.getCity()) : getAllTraders();

        // Get suggested items for all traders
        for (String traderId : allTraders) {
            if (traderId.equals(thisTraderId)) {
                continue;
            }
            Trader trader = getTrader(traderId);
            if (!trader.canTrade())
                continue;
            for (String item : trader.getWishlist()) {
                if (thisTraderItems.contains(item)) {
                    String[] items = {thisTraderId, traderId, item};
                    result.add(items);
                }
            }
        }

        return result;
    }


    /**
     * Suggests a lend where thisTraderId is lending some item that is in the other trader's wishlist.
     * If such a trade does not exist, return null.
     * @param thisTraderId the id of the trader asking for the suggestion
     * @param inCity true if you desire to search for other traders within the same city as the original trader
     * @return A lend suggestion in the form [fromUserId, toUserId, itemIdToLend]
     * @throws UserNotFoundException if a user was not found
     * @throws AuthorizationException if the given trader id represents a non-trader object.
     */
    @Override
    public String[] suggestLend(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        List<String[]> lends = suggestLendList(thisTraderId, inCity);
        if (lends.size() == 0) {
            return null;
        }
        return new String[]{lends.get(0)[0], lends.get(0)[1], lends.get(0)[2]};
    }

    /**
     * Suggests a trade where thisTraderId is offering some item that is in the other trader's wishlist, and the other
     * trader is offering some item in thisTraderId's wish list.
     * If such a trade does not exist, return null.
     * @param thisTraderId the id of the trader asking for the suggestion
     * @param inCity true if you desire to search for other traders within the same city as the original trader
     * @return A trade suggestion in the form of [fromUserId, toUserId, lendItemId, receiveItemId]
     * @throws UserNotFoundException if a user was not found
     * @throws AuthorizationException if the given trader id represents a non-trader object.
     */
    @Override
    public String[] suggestTrade(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {

        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        List<String[]> toLend = suggestLendList(thisTraderId, inCity);

        HashSet<String> thisTraderWishlist = new HashSet<>(thisTrader.getWishlist());

        // Create trades where both traders give an item that is in each other's wish list
        for (String[] lendInfo : toLend) {
            for (String candidateItem : getTrader(lendInfo[1]).getAvailableItems()) {
                if (thisTraderWishlist.contains(candidateItem)) {
                    return new String[]{lendInfo[0], lendInfo[1], lendInfo[2], candidateItem};
                }
            }
        }
        return null;
    }
}

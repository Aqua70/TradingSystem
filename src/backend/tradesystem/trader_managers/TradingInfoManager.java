package backend.tradesystem.trader_managers;

import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.tradesystem.Manager;
import backend.tradesystem.suggestion_strategies.SuggestionStrategy;

import java.io.IOException;
import java.util.*;

/**
 * Handles anything about getting info on trading and tradable items, but this does not handle trading itself
 */
public class TradingInfoManager extends Manager {
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TradingInfoManager() throws IOException {
        super();
    }



    /**
     * Return trader ids that contain name
     *
     * @param name the string to search for
     * @return traders with similar names
     */
    public List<String> searchTrader(String name) {
        List<String> similarTraders = new ArrayList<>();
        for (String userId : getAllUsers()) {
            try {
                if (getUser(userId) instanceof Trader && getUser(userId).getUsername().toLowerCase().contains(name.toLowerCase()))
                    similarTraders.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return similarTraders;
    }




    /**
     * Gets tradable item ids that has the name substring within the input name string
     * For example, if the item name is "Apple Pie", and the name to check for is "apple",
     * then that TradableItem is included as a list of items to return
     *
     * @param name the name to check for
     * @return list of tradable item ids that match the name
     */
    public List<String> getTradableItemsWithName(String name) {
        List<String> items = new ArrayList<>();
        for (String userId : getAllUsers()) {
            try {
                if (getUser(userId) instanceof Trader) {
                    for (String id : ((Trader) getUser(userId)).getAvailableItems()) {
                        try {
                            TradableItem item = getTradableItem(id);
                            if (item.getName().toLowerCase().contains(name.toLowerCase()))
                                items.add(item.getId());
                        } catch (TradableItemNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return items;
    }




    /**
     * Return the 3 most frequent traders that this trader has traded with.
     *
     * @param traderId the trader being checked for
     * @return a Trader list of the 3 most frequently traded with Traders
     * @throws AuthorizationException if the traderid does not represent a trader
     * @throws UserNotFoundException  user not found
     * @throws TradeNotFoundException trade not found
     */
    public List<String> getFrequentTraders(String traderId) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        List<String> frequentTraders = new ArrayList<>();
        List<String> traders = new ArrayList<>();


        for (String tradeId : getTrader(traderId).getCompletedTrades()) {
            Trade trade = getTrade(tradeId);
            if (trade.getFirstUserId().equals(traderId)) traders.add(trade.getSecondUserId());
            else traders.add(trade.getFirstUserId());
        }

        Set<String> distinct = new HashSet<>(traders);
        for (int i = 0; i < 3; i++) {
            int highest = 0;
            if (distinct.size() == 0) {
                break;
            }
            for (String traderID : distinct) {
                int possibleHigh = Collections.frequency(traders, traderID);
                if (possibleHigh > highest) {
                    if (frequentTraders.size() == i)
                        frequentTraders.add(null);
                    frequentTraders.set(i, traderID);
                    highest = possibleHigh;
                }
            }
            distinct.remove(frequentTraders.get(i));

        }
        return frequentTraders;
    }


    /**
     * Gets a list of the items used in trades
     *
     * @param traderId the trader id
     * @return list of tradable items ids that were recently traded
     * @throws AuthorizationException trader not allowed to get recently traded items
     * @throws TradeNotFoundException trade not found
     * @throws UserNotFoundException  trader id is bad
     */
    public List<String> getRecentTradeItems(String traderId) throws AuthorizationException, TradeNotFoundException,
            UserNotFoundException {
        Trader trader = getTrader(traderId);
        List<String> completedTrades = trader.getCompletedTrades();
        Map<String, Boolean> used = new HashMap<>();
        List<String> recentTradeItems = new ArrayList<>();
        for (int i = completedTrades.size() - 1; i >= 0; i--) {
            String tradeID = completedTrades.get(i);
            Trade trade = getTrade(tradeID);
            String firstItemId = trade.getFirstUserOffer();
            String secondItemId = trade.getSecondUserOffer();

            if (!used.getOrDefault(firstItemId, false) && !firstItemId.equals("")) {
                recentTradeItems.add(firstItemId);
                used.put(firstItemId, true);
            }
            if (!used.getOrDefault(secondItemId, false) && !secondItemId.equals("")) {
                recentTradeItems.add(secondItemId);
                used.put(secondItemId, true);
            }
        }
        return recentTradeItems;
    }

    /**
     * Returns the suggestion provided by a suggest lend algorithm
     *
     * @param traderId trader id
     * @param strategy The lending strategy to use
     * @param inCity   whether to filter for city or not
     * @return A lend suggestion in the form [fromUserId, toUserId, itemIdToLend]
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    public String[] suggestLend(String traderId, boolean inCity, SuggestionStrategy strategy) throws
            UserNotFoundException, AuthorizationException {
        return strategy.suggestLend(traderId, inCity);
    }


    /**
     * Returns the suggestion provided by a suggest trade algorithm
     *
     * @param traderId trader id
     * @param strategy The trading strategy to use
     * @param inCity   whether to filter for city or not
     * @return A trade suggestion in the form of [fromUserId, toUserId, lendItemId, receiveItemId]
     * @throws UserNotFoundException  bad trader ids
     * @throws AuthorizationException can't suggest because user is not a trader or is frozen
     */
    public String[] suggestTrade(String traderId, boolean inCity, SuggestionStrategy strategy) throws
            UserNotFoundException, AuthorizationException {
        return strategy.suggestTrade(traderId, inCity);
    }


}

package backend.tradesystem.suggestion_strategies;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A suggestion strategy which can be used to suggest trades where the items traded are similar (not the same) to items
 * in each trader's wishlist, so each trader will be more or less content with what they will receive
 */
public class SimilarWishlistSuggestion extends Manager implements SuggestionStrategy {



    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public SimilarWishlistSuggestion() throws IOException {
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
     * finds the most similar string in list to the string of the inputted name and returns it if it passes the threshold
     *
     * @param nameId is the id of the item we wish to find a similar name of
     * @param list is the list of strings that we are traversing through
     * @return an array with two cells containing the items name and the score of how similar it is (if the similarity score passes threshold)
     */
    private Object[] similarSearch(String nameId, List<String> list) throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException {

        if (list.size() == 0) {
            return new Object[]{null, 0};
        }
        List<Object[]> similarNames = new ArrayList<>();

        //This is to check what type of list the parameter list is so that this function can work with traders
        // and tradable items
        boolean isListOfTraders = false;
        for (String traderIds : getAllTraders()) {
            if (traderIds.equals(list.get(0))) {
                isListOfTraders = true;
                break;
            }
        }
        /*
        Goes through all items in list and finds similarity score
        The score is calculated like this, for every char in otherNames, we traverse name.length() more chars
        and find how many match, then store the max number of char matches so that we have the max matches for every otherName
        in the list. We put that into an array and find the otherName with the highest number of matches which is the most similar string
         */
        String name;
        if (isListOfTraders) { //this is here to allow similarSearch to work with traders and tradableItems
            name = getTrader(nameId).getUsername();
        } else {
            name = getTradableItem(nameId).getName();
        }
        String longestWord = "";
        for (String otherNamesId : list) {
            String otherNames;
            if (isListOfTraders) {
                otherNames = getTrader(otherNamesId).getUsername();
            } else {
                otherNames = getTradableItem(otherNamesId).getName();
            }

            //we don't want the exact item in the wishlist, b/c that would always be the most similar so if its the same item it skips over it
            if (!otherNamesId.equals(nameId)) {
                int maxSim = 0;
                String[] otherNameWords = otherNames.split("\\s+");
                String[] thisNameWords = name.split("\\s+");

                for (String otherNameWord : otherNameWords) {//compares every single word in otherWord to every single word in the string we are searching for
                    for (String thisNameWord : thisNameWords) {

                        String longerName; //these are needed to fix bug when comparing strings with different sizes
                        String shorterName;

                        if (otherNameWord.length() < thisNameWord.length()) {
                            longerName = thisNameWord;
                            shorterName = otherNameWord;
                        } else {
                            shorterName = thisNameWord;
                            longerName = otherNameWord;
                        }

                        if (longerName.length() > longestWord.length()) { //needed for threshold
                            longestWord = longerName;
                        }

                        for (int k = 0; k < longerName.length(); k++) {//Finds the maximum similarity score for each word in list
                            int similarities = 0;
                            int k2 = k;
                            int l = 0;
                            while (l < shorterName.length() && k2 < longerName.length()) {
                                if (Character.toLowerCase(shorterName.charAt(l)) == Character.toLowerCase(longerName.charAt(k2))) {
                                    similarities++;
                                }
                                l++;
                                k2++;
                            }
                            if (similarities > maxSim) {
                                maxSim = similarities;
                            }
                        }
                        //when you add an extra char(name = apple, otherName = appxle) or subtract an extra char, the above algorithm
                        // does not work properly so we need another algorithm below
                        //ideally if name = apple and otherName = appxle then the similarity score should be 4
                        //if name = apple and other otherName = appe then the similarity score should be 4

                        //THE ENTIRE SECTION BELOW IS FOR THE ABOVE TWO TEST CASES...
                        int similarities2 = 0;
                        int endOfShortWord = shorterName.length() - 1;
                        int endOfLongWord = longerName.length() - 1;
                        int k = 0;
                        while ((k < shorterName.length()) && Character.toLowerCase(shorterName.charAt(k)) == Character.toLowerCase((longerName.charAt(k)))) {
                            similarities2++;
                            k++;
                        }
                        while (Character.toLowerCase(shorterName.charAt(endOfShortWord)) == Character.toLowerCase((longerName.charAt(endOfLongWord)))) {
                            similarities2++;
                            endOfShortWord--;
                            endOfLongWord--;
                            if (endOfShortWord == 0) {
                                break;
                            }
                        }
                        similarities2 = Math.min(shorterName.length(), similarities2); //deals with when both words are the same
                        similarities2 = similarities2 - (longerName.length() - shorterName.length());

                        if (similarities2 > maxSim) {
                            maxSim = similarities2;
                        }
                    }
                }
                similarNames.add(new Object[]{otherNames, maxSim, otherNamesId});
            }
        }

        //finds the max similarity score in similarNames
        int max = 0;
        String mostSimilarName = "";
        String mostSimilarNameId = "";
        for (Object[] simNameArr : similarNames) {
            int x = (int) simNameArr[1];
            String similarName = (String) simNameArr[0];
            if (x > max || x == max && (Math.abs(similarName.length() - name.length()) < (Math.abs(mostSimilarName.length() - name.length())))) {
                max = x;
                mostSimilarName = similarName;
                mostSimilarNameId = (String) simNameArr[2];
            }
        }

        //adds a threshold, so that items we consider not similar don't get added, even if there is nothing else
        if (max >= ((int) (longestWord.length() * 0.8))) {
            return new Object[]{mostSimilarNameId, max};
        }

        return null;
    }


    /**
     * Suggests a lend where thisTraderId is lending some item that is similar to one in the other trader's wishlist.
     * If such a trade does not exist, return null.
     * @param thisTraderId is the id of the trader
     * @param inCity if the user wants to filter for city
     * @return A lend suggestion in the form [fromUserId, toUserId, itemIdToLend]
     * @throws AuthorizationException if thisTraderId is not a trader
     */
    @Override
    public String[] suggestLend(String thisTraderId, boolean inCity) throws UserNotFoundException, AuthorizationException {
        List<String> allTraders = getAllTraders();
        Trader thisTrader = getTrader(thisTraderId);
        allTraders.remove(thisTraderId); //so it doesn't trade with itself
        String city = thisTrader.getCity();

        String mostSimItemId = null;
        String mostSimTraderId = null;
        int maxSim = 0;
        for (String otherTraderId : allTraders) {
            Trader otherTrader = getTrader(otherTraderId);
            if (!otherTrader.canTrade() || inCity && !(otherTrader.getCity().equalsIgnoreCase(city))) {
                continue;
            }
            for(String inventoryItemId: thisTrader.getAvailableItems()){
                Object[] giveItem = null;
                try {
                    giveItem = similarSearch(inventoryItemId, otherTrader.getWishlist());
                } catch (TradableItemNotFoundException ignored) {
                } finally {
                    if (!(giveItem == null)) {
                        if (((int) giveItem[1]) > maxSim) {
                            mostSimItemId = inventoryItemId;
                            mostSimTraderId = otherTraderId;
                            maxSim = ((int) giveItem[1]);
                        }
                    }
                }
            }
        }
        if(mostSimItemId == null || mostSimTraderId == null){
            return null;
        }
        return new String[]{thisTraderId, mostSimTraderId, mostSimItemId};
    }

    /**
     * Suggests a trade where thisTraderId is offering some item that is similar to one in the other trader's wishlist, and the other
     * trader is offering some item similar to one in thisTraderId's wish list.
     * If such a trade does not exist, return null.
     * @param thisTraderId the id of the trader asking for the suggestion
     * @param inCity true if you desire to search for other traders within the same city as the original trader
     * @return A trade suggestion in the form of [fromUserId, toUserId, lendItemId, receiveItemId]
     * @throws UserNotFoundException if a user was not found
     * @throws AuthorizationException if the given trader id represents a non-trader object.
     */
    @Override
    public String[] suggestTrade(String thisTraderId, boolean inCity) throws UserNotFoundException, AuthorizationException {
        //Finds the most similar trade, most similar is calculated through similarSearch

        List<String> allTraders = getAllTraders();
        allTraders.remove(thisTraderId);
        Trader thisTrader = getTrader(thisTraderId);
        String city = thisTrader.getCity();
        int maxTotalSim = 0;
        String mostSimGetItemId = null;
        String mostSimGiveItemId = null;
        String mostSimTraderId = null;

        for (String otherTraderId : allTraders) {
            Trader otherTrader = getTrader(otherTraderId);
            if (!otherTrader.canTrade() || inCity && !(otherTrader.getCity().equalsIgnoreCase(city))) {
                continue;
            }
            String simGetItemId = null;
            int maxGetSim = 0;
            String simGiveItemId = null;
            int maxGiveSim = 0;

            //finds the item that thisTrader wants the most from otherTrader
            for (String wishlistItemId : thisTrader.getWishlist()) {
                Object[] getItem = null;
                try {
                    getItem = similarSearch(wishlistItemId, otherTrader.getAvailableItems());
                } catch (TradableItemNotFoundException ignored) {
                } finally {
                    if (!(getItem == null)) {
                        if (((int) getItem[1]) > maxGetSim) {
                            simGetItemId = (String) getItem[0];
                            maxGetSim = ((int) getItem[1]);
                        }
                    }
                }
            }

            //finds the item that otherTrader wants the most from thisTrader
            for (String otherTraderWishlistItemId : otherTrader.getWishlist()) {

                Object[] giveItem = null;
                try {
                    giveItem = similarSearch(otherTraderWishlistItemId, thisTrader.getAvailableItems());
                } catch (TradableItemNotFoundException ignored) {
                } finally {
                    if (!(giveItem == null)) {
                        if (((int) giveItem[1]) > maxGiveSim) {
                            simGiveItemId = (String) giveItem[0];

                            maxGiveSim = ((int) giveItem[1]);
                        }
                    }
                }
            }
            if (maxGetSim + maxGiveSim > maxTotalSim && (maxGetSim != 0 && maxGiveSim != 0)) {
                maxTotalSim = maxGetSim + maxGiveSim;
                mostSimGetItemId = simGetItemId;
                mostSimGiveItemId = simGiveItemId;
                mostSimTraderId = otherTraderId;

            }
        }

        if (mostSimTraderId == null || mostSimGetItemId == null || mostSimGiveItemId == null)
            return null;

        return new String[]{thisTraderId, mostSimTraderId, mostSimGiveItemId, mostSimGetItemId};
    }
}

package backend.models.users;


import backend.models.Review;
import backend.tradesystem.TraderProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * represents a trader (user who can trade)
 */

public class Trader extends User implements Serializable {
    private final List<String> wishList = new ArrayList<>();  // Items that this trader wants.
    private final List<String> availableItems = new ArrayList<>(); // Items that the trader is willing to trade,lend etc
    private final List<String> requestedItems = new ArrayList<>(); // Items that this trader wishes to be added to availableItems list
    private final List<String> ongoingItems = new ArrayList<>(); // Items that this trader is currently using for trades

    private final List<String> acceptedTrades = new ArrayList<>(); // Trades that are ongoing
    private final List<String> requestedTrades = new ArrayList<>(); // Trades yet to be accepted or denied
    private final List<String> completedTrades = new ArrayList<>(); // Trades where meetings are finished and confirmed by both sides and transaction has concluded
    private final List<Review> reviews = new ArrayList<>(); // List of reviews
    private int tradeLimit; // This trader's trade limit (total amount of trades that can be conducted per week)
    private int incompleteTradeLim; // This trader's incomplete trade limit
    private int totalItemsLent;
    private int minimumAmountNeededToBorrow; // The minimum value totalItemsLent - totalItemsBorrowed needs to be to borrow
    private int tradeCount;
    private boolean isIdle = false;
    private String city;
    private int totalItemsBorrowed;
    private int totalAcceptedBorrows; // This trader's amount of trades they accepted which are borrows



    /**
     * Constructs a trader with its own username and password.
     *
     * @param name                        the trader's username
     * @param password                    the trader's password
     * @param tradeLimit                  number of trades that can be done
     * @param incompleteTradeLim          the limit for how many incomplete trades can be done
     * @param minimumAmountNeededToBorrow the minimum amount of items that must be lent before borrowing is allowed
     * @param city                        the city of the trader
     */
    public Trader(String name, String password, String city, int tradeLimit, int incompleteTradeLim, int minimumAmountNeededToBorrow) {
        super(name, password);
        this.tradeLimit = tradeLimit;
        this.incompleteTradeLim = incompleteTradeLim;
        this.minimumAmountNeededToBorrow = minimumAmountNeededToBorrow;
        this.city = city;

    }

    /**
     * Return this trader's totalAcceptedBorrows
     * @return this trader's totalAcceptedBorrows
     */
    public int getTotalAcceptedBorrows() {
        return totalAcceptedBorrows;
    }

    /**
     * Sets this trader's totalAcceptedBorrows
     * @param totalAcceptedBorrows the new value for this trader's totalAcceptedBorrows
     */
    public void setTotalAcceptedBorrows(int totalAcceptedBorrows) {
        this.totalAcceptedBorrows = totalAcceptedBorrows;
    }

    /**
     *  Return whether this trader can accept a borrow request
     * @return whether this trader can accept a borrow request
     */
    public boolean canAcceptBorrow(){
        return canTrade() && totalItemsLent - totalAcceptedBorrows - totalItemsBorrowed >= minimumAmountNeededToBorrow;
    }

    /**
     * Gets all ongoing items
     * @return all ongoing items
     */
    public List<String> getOngoingItems(){
        return ongoingItems;
    }

    /**
     * Gets all reviews
     * @return all reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Add a review
     * @param review the review being added
     */
    public void addReview(Review review){
        reviews.add(review);
    }

    /**
     * the city of the trader
     *
     * @return city of the trader
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the trader
     *
     * @param city city of the trader
     */
    public void setCity(String city) {
        this.city = city;
    }


    /**
     * Makes the trader have an idle status
     *
     * @param idle whether the trader is idle
     */
    public void setIdle(boolean idle) {
        isIdle = idle;
    }

    /**
     * if the user is idle
     *
     * @return if the user is idle
     */
    public boolean isIdle() {
        return isIdle;
    }


    /**
     * If the trader can borrow
     *
     * @return if the trader can borrow
     */
    public boolean canBorrow() {
        return canTrade() && totalItemsLent - totalItemsBorrowed >= minimumAmountNeededToBorrow;
    }

    /**
     * if the trader can trade
     *
     * @return if the trader can trade
     */
    public boolean canTrade() {
        return !isFrozen() && tradeCount < tradeLimit && !isIdle();
    }


    /**
     * total completed trade count
     *
     * @return total completed trade count
     */
    public int getTradeCount() {
        return tradeCount;
    }

    /**
     * Sets the value of this user's tradeCount
     *
     * @param tradeCount the new value of this user's tradeCount
     */
    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }


    /**
     * the trader's wishlist
     *
     * @return the trader's wishlist
     */

    public List<String> getWishlist() {
        return wishList;
    }

    /**
     * list of available items this trader has
     *
     * @return list of available items this trader has
     */

    public List<String> getAvailableItems() {
        return availableItems;
    }


    /**
     * list of items this trader requested to borrow/trade
     *
     * @return list of items this trader requested to borrow/trade
     */


    public List<String> getRequestedItems() {
        return requestedItems;
    }


    /**
     * list of trades accepted
     *
     * @return list of trades accepted
     */


    public List<String> getAcceptedTrades() {
        return acceptedTrades;
    }

    /**
     * list of trades that are completed (ie confirmed by both users)
     *
     * @return list of trades that are completed (ie confirmed by both users)
     */


    public List<String> getCompletedTrades() {
        return completedTrades;
    }


    /**
     * list of trades requested by this trader
     *
     * @return list of trades requested by this trader
     */


    public List<String> getRequestedTrades() {
        return requestedTrades;
    }



    /**
     * total number of items borrowed by the trader
     *
     * @return total number of items borrowed by the trader
     */
    public int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    /**
     * total number of items borrowed by the trader
     *
     * @param totalItemsBorrowed total number of items borrowed by the trader
     */
    public void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    /**
     * total number of items lent by the trader
     *
     * @return total number of items lent by the trader
     */


    public int getTotalItemsLent() {
        return totalItemsLent;
    }

    /**
     * set a new value to total number of items lent by this trader
     *
     * @param totalItemsLent total number of items lent by the trader
     */
    public void setTotalItemsLent(int totalItemsLent) {
        this.totalItemsLent = totalItemsLent;
    }

    /**
     * Sets the value of a specific limit
     * @param limit the limit to change
     * @param newValue the new value of the limit
     */
    public void setLimit(TraderProperties limit, int newValue){
        switch(limit){
            case INCOMPLETE_TRADE_LIM:
                this.incompleteTradeLim = newValue;
                break;
            case MINIMUM_AMOUNT_NEEDED_TO_BORROW:
                this.minimumAmountNeededToBorrow = newValue;
                break;
            default:
                this.tradeLimit = newValue;
        }
    }

    /**
     * Return whether this trader has surpassed the incomplete trade limit
     * @return whether this trader has surpassed the incomplete trade limit
     */
    public boolean hasSurpassedIncompleteTradeLimit(){
        return acceptedTrades.size() > incompleteTradeLim;
    }

}

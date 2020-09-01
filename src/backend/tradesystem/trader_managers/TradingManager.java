package backend.tradesystem.trader_managers;


import backend.exceptions.*;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.*;

/**
 * Used for trading
 */
public class TradingManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TradingManager() throws IOException {
        super();
    }

    /**
     * Adds a new trade to the system and acknowledges that it is a requested trade between two users in the trade.
     * A trade is a borrow if the the item the first user is offering is "".
     * A trade is a lend if the item the second user is offering is "".
     *
     * @param traderId1         The id of the trader sending the trade
     * @param traderId2         The id of the trader receiving the trade
     * @param meetingTime       The first meeting time for the trade
     * @param secondMeetingTime The second meeting time for the trade. Null for this means the trade is permanent
     * @param location          The location the trade will occur
     * @param firstUserOfferId  The trade sender's offer (empty string for trader1 to offer nothing)
     * @param secondUserOfferId The trade receiver's offer (empty string for trader2 to offer nothing)
     * @param allowedEdits      The amount of times each trader can edit the trade
     * @param message           The message that will be sent along with this trade
     * @return the trade object's id
     * @throws UserNotFoundException  the user that wants to be traded with doesn't exist
     * @throws AuthorizationException the item for trading cannot be traded
     * @throws CannotTradeException   cannot request a trade
     */
    public String requestTrade(String traderId1, String traderId2, Date meetingTime, Date secondMeetingTime, String location,
                               String firstUserOfferId, String secondUserOfferId, int allowedEdits, String message)
            throws UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader trader = getTrader(traderId1);
        Trader secondTrader = getTrader(traderId2);
        if (traderId2.equals(traderId1)) throw new CannotTradeException("Cannot trade with yourself");
        if (firstUserOfferId.equals("") && secondUserOfferId.equals("")) {
            throw new CannotTradeException("You must add items to the trade");
        }
        // If neither trader can trade, throw an exception
        if (!trader.canTrade())
            throw new CannotTradeException("You cannot trade due to trading restrictions");
        if (!secondTrader.canTrade())
            throw new CannotTradeException("The user requested cannot trade due to trading restrictions");
        if (firstUserOfferId.equals("") && !trader.canBorrow())
            throw new CannotTradeException("You have not lent enough to borrow");

        // This is used to check if the items are in each user's inventory
        if (!hasItem(trader, firstUserOfferId) || !hasItem(secondTrader, secondUserOfferId))
            throw new AuthorizationException("The trade offer contains an item that the user does not have");

        // Check whether the two dates are valid.
        if (!datesAreValid(meetingTime, secondMeetingTime)) {
            throw new CannotTradeException("The suggested date(s) are not possible");
        }

        // Check whether the trader has too many incomplete trades pending
        if (trader.hasSurpassedIncompleteTradeLimit() || secondTrader.hasSurpassedIncompleteTradeLimit()) {
            throw new CannotTradeException("One of the two users has too many active trades.");
        }
        Trade trade = new Trade(traderId1, traderId2, meetingTime, secondMeetingTime,
                location, firstUserOfferId, secondUserOfferId, allowedEdits, message);

        // This trade has now been requested, so add it to the requested trades of each trader
        trader.getRequestedTrades().add(trade.getId());
        secondTrader.getRequestedTrades().add(trade.getId());

        updateUserDatabase(trader);
        updateTradeDatabase(trade);
        updateUserDatabase(secondTrader);
        return trade.getId();
    }

    /**
     * For accepting a trade request
     *
     * @param traderId the trader accepting the request
     * @param tradeId  the trade id
     * @return true if the request was accepted
     * @throws TradeNotFoundException if the trade wasn't found
     * @throws AuthorizationException if the user is not a trader
     * @throws UserNotFoundException  if the user doesn't exist
     * @throws CannotTradeException   if trading limitations prevent the trade from happening
     */
    public boolean acceptRequest(String traderId, String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException, CannotTradeException {
        Trade trade = getTrade(tradeId);
        if (!trade.isTraderInTrade(traderId))
            throw new AuthorizationException("This trader doesn't belong to this trade");
        Trader trader = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());

        // Check that this trader has the ability to accept this trade
        if (!trader.canTrade() || !trader2.canTrade())
            throw new CannotTradeException("Trade limitations prevent this trade from being accepted");
        if (trade.getFirstUserOffer().equals("") && !trader.canAcceptBorrow()) {
            throw new CannotTradeException("The trader who originally sent the trade can't borrow");
        }
        // Check to see that the items are available to trade
        if (!hasItem(trader, trade.getFirstUserOffer()) || !hasItem(trader2, trade.getSecondUserOffer())) {
            throw new CannotTradeException("One of the traders no longer has the required item for the trade");
        }

        if (trade.getFirstUserId().equals(traderId))
            trade.setHasFirstUserConfirmedRequest(true);
        else
            trade.setHasSecondUserConfirmedRequest(true);

        updateUserDatabase(trader);
        updateUserDatabase(trader2);

        // If both users accepted then move items out of the inventory
        if (trade.isHasFirstUserConfirmedRequest() && trade.isHasSecondUserConfirmedRequest()) {
            trader.getAvailableItems().remove(trade.getFirstUserOffer());
            trader2.getAvailableItems().remove(trade.getSecondUserOffer());
            if (!trade.getFirstUserOffer().equals(""))
                trader.getOngoingItems().add(trade.getFirstUserOffer());
            else
                trader.setTotalAcceptedBorrows(trader.getTotalAcceptedBorrows() + 1);

            if (!trade.getSecondUserOffer().equals(""))
                trader2.getOngoingItems().add(trade.getSecondUserOffer());

            trader.getAcceptedTrades().add(tradeId);
            trader2.getAcceptedTrades().add(tradeId);
            trader.getRequestedTrades().remove(tradeId);
            trader2.getRequestedTrades().remove(tradeId);
            trader.setTradeCount(trader.getTradeCount() + 1);
            trader2.setTradeCount(trader2.getTradeCount() + 1);
            updateUserDatabase(trader);
            updateUserDatabase(trader2);
            removeInvalidRequests();
            return true;
        }
        return false;
    }

    /**
     * Confirms the first meeting
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the user doesn't exist
     */
    private void confirmFirstMeeting(String traderId, String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (!trade.isTraderInTrade(traderId))
            throw new AuthorizationException("This trader doesn't belong to this trade");
        trade.setUserConfirmed(traderId);

        // If both users confirmed the first meeting meeting...
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1()) {
            Trader trader1 = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());

            trader1.getOngoingItems().remove(trade.getFirstUserOffer());
            trader2.getOngoingItems().remove(trade.getSecondUserOffer());

            trader1.getWishlist().remove(trade.getSecondUserOffer());
            trader2.getWishlist().remove(trade.getFirstUserOffer());

            // If the trade happened to be permanent...
            if (trade.getSecondMeetingTime() == null) {
                trader1.getAcceptedTrades().remove(tradeId);
                trader2.getAcceptedTrades().remove(tradeId);
                trader1.getCompletedTrades().add(tradeId);
                trader2.getCompletedTrades().add(tradeId);

                // Add the necessary items to each traders inventory
                if (!trade.getSecondUserOffer().equals(""))
                    trader1.getAvailableItems().add(trade.getSecondUserOffer());
                if (!trade.getFirstUserOffer().equals(""))
                    trader2.getAvailableItems().add(trade.getFirstUserOffer());

                if (trade.getFirstUserOffer().equals("")) {
                    trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
                    trader1.setTotalAcceptedBorrows(trader1.getTotalAcceptedBorrows() - 1);
                }
                if (trade.getSecondUserOffer().equals(""))
                    trader1.setTotalItemsLent(trader1.getTotalItemsLent() + 1);
            } else {
                if (!trade.getSecondUserOffer().equals(""))
                    trader1.getOngoingItems().add(trade.getSecondUserOffer());
                if (!trade.getFirstUserOffer().equals(""))
                    trader2.getOngoingItems().add(trade.getFirstUserOffer());
            }

            updateUserDatabase(trader1);
            updateUserDatabase(trader2);
        }
        updateTradeDatabase(trade);
    }

    /**
     * Confirms the second meeting.
     * This method assumes that the items required for the trade to be conducted are still in the ongoing items of each
     * trader (this should generally be true unless ongoing items was tampered with).
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the other user of the trade is not found
     */
    private void confirmSecondMeeting(String traderId, String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        Trader trader1 = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());
        if (trade.getSecondMeetingTime() == null) return;

        if (!trade.isTraderInTrade(traderId))
            throw new AuthorizationException("This trader doesn't belong to this trade");
        if (!trade.isFirstUserConfirmed1() || !trade.isSecondUserConfirmed1()) {
            throw new AuthorizationException("First meeting hasn't been confirmed");
        }
        trade.setUserConfirmed(traderId);

        // If the second meeting has been confirmed...
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1() &&
                trade.isFirstUserConfirmed2() && trade.isSecondUserConfirmed2()) {

            trader1.getCompletedTrades().add(tradeId);
            trader2.getCompletedTrades().add(tradeId);
            trader1.getAcceptedTrades().remove(tradeId);
            trader2.getAcceptedTrades().remove(tradeId);

            // Update available items of first user / borrowed count
            if (!trade.getFirstUserOffer().equals("")) {
                trader1.getAvailableItems().add(trade.getFirstUserOffer());
                trader2.getOngoingItems().remove(trade.getFirstUserOffer());
            } else {
                trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
                trader1.setTotalAcceptedBorrows(trader1.getTotalAcceptedBorrows() - 1);
            }

            // Update available items of the second trader / lent item count
            if (!trade.getSecondUserOffer().equals("")) {
                trader2.getAvailableItems().add(trade.getSecondUserOffer());
                trader1.getOngoingItems().remove(trade.getSecondUserOffer());
            } else
                trader1.setTotalItemsLent(trader1.getTotalItemsLent() + 1);


            trader1.getWishlist().remove(trade.getFirstUserOffer());
            trader2.getWishlist().remove(trade.getSecondUserOffer());
            updateUserDatabase(trader1);
            updateUserDatabase(trader2);
        }
        updateTradeDatabase(trade);
    }

    /**
     * Confirms either the first or second meeting depending on which one is not confirmed.
     * This method makes sure that the user can only confirm a second meeting once both traders
     * have confirmed the first.
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the other user of the trade is not found
     */
    public void confirmMeetingGeneral(String traderId, String tradeId) throws TradeNotFoundException,
            AuthorizationException, UserNotFoundException {
        Trade t = getTrade(tradeId);
        if (t.isFirstUserConfirmed1() && t.isSecondUserConfirmed1()) {
            confirmSecondMeeting(traderId, tradeId);
        } else {
            confirmFirstMeeting(traderId, tradeId);
        }
    }

    /**
     * Sending a counter offer
     *
     * @param traderId          the trader id sending the counter offer
     * @param tradeId           the trade id
     * @param meetingTime       the new time of the trade
     * @param secondMeetingTime the second meeting time of the trade
     * @param meetingLocation   the meeting location of the trade
     * @param thisTraderOffer   the tradable item id that the current trader is offering
     * @param thatTraderOffer   the tradable item id that the current trader wants from the other trader
     * @param message           message of the offer
     * @return the id of the trade
     * @throws CannotTradeException   too many edits
     * @throws TradeNotFoundException this trade doesn't exist
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  user wasn't found
     */
    public String counterTradeOffer(String traderId, String tradeId, Date meetingTime, Date secondMeetingTime, String
            meetingLocation, String thisTraderOffer, String thatTraderOffer, String message) throws
            CannotTradeException, TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        Trader trader1 = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());

        if (!trade.isTraderInTrade(traderId))
            throw new AuthorizationException("This trader doesn't belong to this trade");
        if (!getTrader(trade.getFirstUserId()).canTrade() || !getTrader(trade.getSecondUserId()).canTrade())
            throw new CannotTradeException("Could not send a counter trade offer, one of the two traders cannot trade");
        if (thisTraderOffer.equals("") && thatTraderOffer.equals("")) {
            throw new CannotTradeException("You must add items to the trade");
        }
        if (trade.getNumEdits() >= trade.getMaxAllowedEdits()) {
            throw new CannotTradeException("Too many edits. Trade is cancelled.");
        }
        if (!datesAreValid(meetingTime, secondMeetingTime)) {
            throw new CannotTradeException("The suggested date(s) are not possible");
        }

        // if the trader sending the request is the first user...
        if (trader1.getId().equals(traderId)) {
            if (!hasItem(trader1, thisTraderOffer) || !hasItem(trader2, thatTraderOffer)) {
                throw new CannotTradeException("One of the traders does not have the required item!");
            }
        } else {
            if (!hasItem(trader2, thisTraderOffer) || !hasItem(trader1, thatTraderOffer)) {
                throw new CannotTradeException("One of the traders does not have the required item!");
            }
        }

        if (trade.getUserTurnToEdit().equals(traderId)) trade.changeUserTurn();
        else throw new CannotTradeException("A previous trade offer has already been sent");
        trade.setMeetingTime(meetingTime);
        trade.setSecondMeetingTime(secondMeetingTime);
        trade.setMeetingLocation(meetingLocation);
        trade.setMessage(message);
        if (trade.getFirstUserId().equals(traderId)) {
            trade.setFirstUserOffer(thisTraderOffer);
            trade.setSecondUserOffer(thatTraderOffer);
            trade.setHasFirstUserConfirmedRequest(true);
            trade.setHasSecondUserConfirmedRequest(false);
        } else {
            trade.setSecondUserOffer(thisTraderOffer);
            trade.setFirstUserOffer(thatTraderOffer);
            trade.setHasFirstUserConfirmedRequest(false);
            trade.setHasSecondUserConfirmedRequest(true);
        }

        trade.setNumEdits(trade.getNumEdits() + 1);
        updateTradeDatabase(trade);
        return trade.getId();
    }


    /**
     * Cancels a trade request
     *
     * @param tradeID The id of the trade request
     * @throws TradeNotFoundException trade doesn't exist in the user's requested trades
     * @throws UserNotFoundException  the trader(s) can not be found
     * @throws AuthorizationException couldn't find a trader type associated with the trade
     */
    public void rescindTradeRequest(String tradeID) throws TradeNotFoundException, UserNotFoundException, AuthorizationException {
        Trade trade = getTrade(tradeID);
        Trader firstTrader = getTrader(trade.getFirstUserId());
        Trader secondTrader = getTrader(trade.getSecondUserId());
        if (!firstTrader.getRequestedTrades().remove(tradeID)) {
            throw new TradeNotFoundException("Trade request wasn't found");
        }
        secondTrader.getRequestedTrades().remove(tradeID);
        deleteTrade(tradeID);
        updateUserDatabase(firstTrader);
        updateUserDatabase(secondTrader);
    }

    /**
     * Undos an ongoing, accepted trade by removing it from the databse and returning the items back to their respective
     * users.
     * This method only should be used carefully since rescinding an ongoing trade after the real life trade has
     * happened has dire consequences.
     *
     * @param tradeID the id of the trade to undo
     * @throws TradeNotFoundException trade doesn't exist in the user's accepted trades
     * @throws UserNotFoundException  the trader(s) can not be found
     * @throws AuthorizationException couldn't find a trader type associated with the trade
     * @throws CannotTradeException   cannot complete this request because the trade could not be reversed
     */
    public void rescindOngoingTrade(String tradeID) throws
            TradeNotFoundException, UserNotFoundException, AuthorizationException, CannotTradeException {

        Trade trade = getTrade(tradeID);
        Trader firstTrader = getTrader(trade.getFirstUserId());
        Trader secondTrader = getTrader(trade.getSecondUserId());
        if (!firstTrader.getAcceptedTrades().contains(trade.getId()))
            throw new CannotTradeException("The trade is not accepted");


        // Remove trades
        firstTrader.getAcceptedTrades().remove(tradeID);
        secondTrader.getAcceptedTrades().remove(tradeID);

        // Add items
        if (!trade.getFirstUserOffer().equals(""))
            firstTrader.getAvailableItems().add(trade.getFirstUserOffer());
        else {
            firstTrader.setTotalAcceptedBorrows(firstTrader.getTotalAcceptedBorrows() - 1);
        }
        if (!trade.getSecondUserOffer().equals(""))
            secondTrader.getAvailableItems().add(trade.getSecondUserOffer());

        firstTrader.getOngoingItems().remove(trade.getFirstUserOffer());
        firstTrader.getOngoingItems().remove(trade.getSecondUserOffer());
        secondTrader.getOngoingItems().remove(trade.getFirstUserOffer());
        secondTrader.getOngoingItems().remove(trade.getSecondUserOffer());
        firstTrader.setTradeCount(firstTrader.getTradeCount() - 1);
        secondTrader.setTradeCount(secondTrader.getTradeCount() - 1);

        // Update database
        deleteTrade(trade.getId());
        updateUserDatabase(firstTrader);
        updateUserDatabase(secondTrader);

    }

    /**
     * Checks whether two given dates are valid
     *
     * @param d1 the first date time
     * @param d2 the second date time
     * @return true if the two dates are valid dates
     */
    private boolean datesAreValid(Date d1, Date d2) {
        return System.currentTimeMillis() <= d1.getTime() && (d2 == null || d1.getTime() < d2.getTime());
    }


    /**
     * Return whether this trader has this item, or if the item is just "" (meaning no item).
     *
     * @param trader the trader who's inventory we're checking
     * @param item   the item we are looking for
     * @return true if the item was found in the trader's inventory
     */
    private boolean hasItem(Trader trader, String item) {
        return (item.equals("") || trader.getAvailableItems().contains(item));
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
                    boolean isValid = hasItem(firstTrader, t.getFirstUserOffer()) && hasItem(secondTrader, t.getSecondUserOffer());

                    isValid = isValid && firstTrader.canTrade() && secondTrader.canTrade();

                    if (t.getFirstUserOffer().equals(""))
                        isValid = isValid && firstTrader.canBorrow();

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

}

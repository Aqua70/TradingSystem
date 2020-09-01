package backend.tradesystem.queries;

import backend.exceptions.TradeNotFoundException;
import backend.models.Trade;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.Date;

/**
 * For getting info about a specific trade
 */
public class TradeQuery extends Manager {

    /**
     * For getting access to database files
     *
     * @throws IOException issues with getting the file path
     */
    public TradeQuery() throws IOException {
        super();
    }


    /**
     * Get the message along with the trade offer
     *
     * @param tradeId The id of the trade which is being checked
     * @return the message
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getMessage(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getMessage();
    }

    /**
     * if the first meeting is confirmed by the first user
     *
     * @param tradeId The id of the trade which is being checked
     * @return if the user that initialized the trade confirmed the first meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isFirstUserConfirmed1(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isFirstUserConfirmed1();
    }

    /**
     * if the user that got sent the trade confirmed the first meeting
     *
     * @param tradeId The id of the trade which is being checked
     * @return if the user that got sent the trade confirmed the first meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isSecondUserConfirmed1(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isSecondUserConfirmed1();
    }

    /**
     * if the user that initialized the trade confirmed the second meeting
     *
     * @param tradeId The id of the trade which is being checked
     * @return if the user that initialized the trade confirmed the second meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isFirstUserConfirmed2(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isFirstUserConfirmed2();
    }

    /**
     * if the user that initialized the trade confirmed the second meeting
     *
     * @param tradeId The id of the trade which is being checked
     * @return if the user that initialized the trade confirmed the second meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isSecondUserConfirmed2(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isSecondUserConfirmed2();
    }

    /**
     * when the first trade is taking place
     *
     * @param tradeId The id of the trade which is being checked
     * @return when the first trade is taking place
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public Date getMeetingTime(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getMeetingTime();
    }

    /**
     * when the second trade is taking place
     *
     * @param tradeId The id of the trade which is being checked
     * @return when the second trade is taking place
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public Date getSecondMeetingTime(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getSecondMeetingTime();
    }

    /**
     * where the trade is taking place
     *
     * @param tradeId The id of the trade which is being checked
     * @return where the trade is taking place
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getMeetingLocation(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getMeetingLocation();
    }


    /**
     * the id of the item that the user that initialized the trade is willing to offer
     *
     * @param tradeId The id of the trade which is being checked
     * @return the id of the item that the user that initialized the trade is willing to offer
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getFirstUserOffer(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getFirstUserOffer();
    }

    /**
     * the id of the item that the user that got sent the trade is willing to offer
     *
     * @param tradeId The id of the trade which is being checked
     * @return the id of the item that the user that got sent the trade is willing to offer
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getSecondUserOffer(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getSecondUserOffer();
    }

    /**
     * the user id of the person's turn to edit the trade
     *
     * @param tradeId The id of the trade which is being checked
     * @return the user id of the person's turn to edit the trade
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getUserTurnToEdit(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getUserTurnToEdit();
    }


    /**
     * the user id of the person initializing the trade
     *
     * @param tradeId The id of the trade which is being checked
     * @return the user id of the person initializing the trade
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getFirstUserId(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getFirstUserId();
    }

    /**
     * the user id of the person the trade is being sent to
     *
     * @param tradeId The id of the trade which is being checked
     * @return the user id of the person the trade is being sent to
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getSecondUserId(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getSecondUserId();
    }

    /**
     * Gets the id of the other user participating in the trade
     * @param tradeId the id of the trade
     * @param traderId the id of one of the users participating in the trade
     * @return the id of the other user participating in the trade
     * @throws TradeNotFoundException if the trade could not be found
     */
    public String getOtherUserId(String tradeId, String traderId) throws TradeNotFoundException {
        Trade t= getTrade(tradeId);
        if (t.getFirstUserId().equals(traderId)){
            return t.getSecondUserId();
        }
        else{
            return t.getFirstUserId();
        }
    }

    /**
     * Gets the amount of edits left for this trade for the next user who can trade
     * @param tradeId the id of the trade
     * @return the amount of edits left for this trade for the next user who can trade
     * @throws TradeNotFoundException if the trade could not be found
     */
    public int getEditAmountLeft(String tradeId) throws TradeNotFoundException {
        Trade t= getTrade(tradeId);
        return (t.getMaxAllowedEdits() - t.getNumEdits()+1)/2;
    }

}

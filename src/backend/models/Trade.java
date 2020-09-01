package backend.models;


import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a trade between two users
 */
public class Trade implements Serializable, Idable {
    private Date meetingTime;
    private Date secondMeetingTime;
    private String meetingLocation;
    private int numEdits = 0;
    private String firstUserOffer;
    private String secondUserOffer;
    private boolean hasFirstUserConfirmedRequest = true; // Whether trader1 has accepted the trade request
    private boolean hasSecondUserConfirmedRequest = false; // Whether trader2 has accepted the trade request
    private boolean isFirstUserConfirmed1 = false; // Whether trader1 has confirmed the first meeting
    private boolean isSecondUserConfirmed1 = false; // Whether trader2 has confirmed the first meeting
    private boolean isFirstUserConfirmed2 = false; // Whether trader1 has confirmed the second meeting
    private boolean isSecondUserConfirmed2 = false; // Whether trader2 has confirmed the second meeting
    private final String FIRST_USER_ID, SECOND_USER_ID;
    private final int MAX_ALLOWED_NUM_EDITS;
    private String message;
    private final String id = UUID.randomUUID().toString();
    private String userTurnToEdit;

    /**
     * Makes a new trade object
     *
     * @param firstUserId       the user id of the person initializing the trade
     * @param secondUserId      the user id of the person the trade is being sent to
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place
     * @param meetingLocation   where the meeting takes place
     * @param firstUserOffer    the item id that the user who initialized the trade is willing to offer
     * @param secondUserOffer   the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @param message           the message that goes with the trade
     */
    public Trade(String firstUserId, String secondUserId,
                 Date meetingTime, Date secondMeetingTime,
                 String meetingLocation, String firstUserOffer, String secondUserOffer, int allowedEdits, String message) {
        super();
        this.FIRST_USER_ID = firstUserId;
        this.SECOND_USER_ID = secondUserId;
        this.MAX_ALLOWED_NUM_EDITS = allowedEdits * 2;
        this.userTurnToEdit = secondUserId;
        this.meetingTime = meetingTime;
        this.secondMeetingTime = secondMeetingTime;
        this.meetingLocation = meetingLocation;
        this.firstUserOffer = firstUserOffer;
        this.secondUserOffer = secondUserOffer;
        this.message = message;
    }

    /**
     * The message along with the trade offer
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message with the trade offer
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * if first meeting is confirmed by the first user
     *
     * @return if the user that initialized the trade confirmed the first meeting
     */
    public boolean isFirstUserConfirmed1() {
        return isFirstUserConfirmed1;
    }

    /**
     * if the user that got sent the trade confirmed the first meeting
     *
     * @return if the user that got sent the trade confirmed the first meeting
     */
    public boolean isSecondUserConfirmed1() {
        return isSecondUserConfirmed1;
    }


    /**
     * if the user that initialized the trade confirmed the second meeting
     *
     * @return if the user that initialized the trade confirmed the second meeting
     */
    public boolean isFirstUserConfirmed2() {
        return isFirstUserConfirmed2;
    }


    /**
     * if the user that initialized the trade confirmed the second meeting
     *
     * @return if the user that initialized the trade confirmed the second meeting
     */
    public boolean isSecondUserConfirmed2() {
        return isSecondUserConfirmed2;
    }

    /**
     * the user id of the person initializing the trade
     *
     * @return the user id of the person initializing the trade
     */
    public String getFirstUserId() {
        return FIRST_USER_ID;
    }

    /**
     * the user id of the person the trade is being sent to
     *
     * @return the user id of the person the trade is being sent to
     */
    public String getSecondUserId() {
        return SECOND_USER_ID;
    }

    /**
     * number of times the trade has been edited
     *
     * @return number of times the trade has been edited
     */
    public int getNumEdits() {
        return numEdits;
    }

    /**
     * the new meeting time for the first meeting
     *
     * @param meetingTime the new meeting time for the first meeting
     */
    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }

    /**
     * the new meeting time for the second meeting
     *
     * @param meetingTime the new meeting time for the second meeting
     */
    public void setSecondMeetingTime(Date meetingTime) {
        secondMeetingTime = meetingTime;
    }

    /**
     * when the first trade is taking place
     *
     * @return when the first trade is taking place
     */
    public Date getMeetingTime() {
        return meetingTime;
    }

    /**
     * when the second trade is taking place
     *
     * @return when the second trade is taking place
     */
    public Date getSecondMeetingTime() {
        return secondMeetingTime;
    }

    /**
     * where the trade is taking place
     *
     * @return where the trade is taking place
     */
    public String getMeetingLocation() {
        return meetingLocation;
    }

    /**
     * new meeting location
     *
     * @param location new meeting location
     */
    public void setMeetingLocation(String location) {
        meetingLocation = location;
    }

    /**
     * the id of the item that the user that initialized the trade is willing to offer
     *
     * @return the id of the item that the user that initialized the trade is willing to offer
     */
    public String getFirstUserOffer() {
        return firstUserOffer;
    }

    /**
     * the id of the item that the user that got sent the trade is willing to offer
     *
     * @return the id of the item that the user that got sent the trade is willing to offer
     */
    public String getSecondUserOffer() {
        return secondUserOffer;
    }

    /**
     * what the first user gives away (id)
     *
     * @param firstUserOffer what the first user gives away (id)
     */
    public void setFirstUserOffer(String firstUserOffer) {
        this.firstUserOffer = firstUserOffer;
    }

    /**
     * what the second user gives away (id)
     *
     * @param secondUserOffer what the second user gives away (id)
     */
    public void setSecondUserOffer(String secondUserOffer) {
        this.secondUserOffer = secondUserOffer;
    }

    /**
     * how many edits can be done
     *
     * @return how many edits can be done
     */
    public int getMaxAllowedEdits() {
        return MAX_ALLOWED_NUM_EDITS;
    }

    /**
     * the user id of the person's turn to edit the trade
     *
     * @return the user id of the person's turn to edit the trade
     */
    public String getUserTurnToEdit() {
        return userTurnToEdit;
    }


    /**
     * Change user's turn to edit trade
     */
    public void changeUserTurn() {
        if (userTurnToEdit.equals(FIRST_USER_ID)) userTurnToEdit = SECOND_USER_ID;
        else userTurnToEdit = FIRST_USER_ID;
    }

    /**
     * number of edits
     *
     * @param num number of edits
     */
    public void setNumEdits(int num) {
        this.numEdits = num;
    }

    /**
     * if the first user has confirmed the trade request
     *
     * @return if the first user has confirmed the trade request
     */
    public boolean isHasFirstUserConfirmedRequest() {
        return hasFirstUserConfirmedRequest;
    }

    /**
     * changing the status if the first user confirmed the trade
     *
     * @param hasFirstUserConfirmedRequest changing the status if the first user confirmed the trade
     */
    public void setHasFirstUserConfirmedRequest(boolean hasFirstUserConfirmedRequest) {
        this.hasFirstUserConfirmedRequest = hasFirstUserConfirmedRequest;
    }

    /**
     * if the second user has confirmed the trade request
     *
     * @return if the second user has confirmed the trade request
     */
    public boolean isHasSecondUserConfirmedRequest() {
        return hasSecondUserConfirmedRequest;
    }

    /**
     * changing the status if the second user confirmed the trade
     *
     * @param hasSecondUserConfirmedRequest changing the status if the second user confirmed the trade
     */
    public void setHasSecondUserConfirmedRequest(boolean hasSecondUserConfirmedRequest) {
        this.hasSecondUserConfirmedRequest = hasSecondUserConfirmedRequest;
    }

    /**
     * checks if the trader is part of this trade
     *
     * @param traderId the trader id
     * @return whether the trader is part of this trade
     */
    public boolean isTraderInTrade(String traderId) {
        return this.getFirstUserId().equals(traderId) || this.getSecondUserId().equals(traderId);
    }

    /**
     * Sets the corresponding trader's confirmed status to true
     *
     * @param traderId the trader who's confirmed status will be updated
     */
    public void setUserConfirmed(String traderId) {
        if (traderId.equals(FIRST_USER_ID)) {
            if (!isFirstUserConfirmed1)
                isFirstUserConfirmed1 = true;
            else if (isSecondUserConfirmed1)
                isFirstUserConfirmed2 = true;
        } else {
            if (!isSecondUserConfirmed1)
                isSecondUserConfirmed1 = true;
            else if (isFirstUserConfirmed1)
                isSecondUserConfirmed2 = true;
        }
    }

    /**
     * Gets the id
     *
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }
}

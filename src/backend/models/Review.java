package backend.models;

import java.io.Serializable;

/**
 * Represents giving something a review
 */
public class Review extends Report implements Serializable {

    private final double RATING;

    /**
     * Makes a new review object
     * @param fromUserId the user that sent the review
     * @param toUserId  the user the review is directed to
     * @param rating the rating of the review
     * @param message the message of the review
     */
    public Review(String fromUserId, String toUserId, double rating, String message) {
        super(fromUserId, toUserId, message);
        this.RATING = rating;
    }

    /**
     * The rating of the review
     * @return The rating of the review
     */
    public double getRating() {
        return RATING;
    }
    /**
     * Gets the id
     * @return the id
     */
    @Override
    public String getId() {
        return super.getId() + RATING;
    }
}

package backend.models;

import java.io.Serializable;

/**
 * Represents what goes into reporting someone
 */
public class Report implements Serializable, Idable {
    private final String FROM_USER_ID;
    private final String REPORT_ON_USER_ID;
    private final String MESSAGE;
    /**
     * Makes a new report
     *
     * @param fromUserId     the user that sent the report
     * @param reportOnUserId the user being reported on
     * @param message        the report description
     */
    public Report(String fromUserId, String reportOnUserId, String message) {
        this.FROM_USER_ID = fromUserId;
        this.REPORT_ON_USER_ID = reportOnUserId;
        this.MESSAGE = message;
    }

    /**
     * The user that sent the report
     *
     * @return The user that sent the report
     */
    public String getFromUserId() {
        return FROM_USER_ID;
    }

    /**
     * The user that got reported on
     *
     * @return the user that got reported on
     */
    public String getReportOnUserId() {
        return REPORT_ON_USER_ID;
    }

    /**
     * Description of the report
     *
     * @return what the report is about
     */
    public String getMessage() {
        return MESSAGE;
    }

    /**
     * Gets the id
     * @return the id
     */
    @Override
    public String getId() {
        return FROM_USER_ID + REPORT_ON_USER_ID + MESSAGE;
    }
}

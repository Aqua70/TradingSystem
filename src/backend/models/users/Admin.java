package backend.models.users;

import backend.models.Report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an admin
 */
public class Admin extends User implements Serializable{


    private List<Report> reports;

    /**
     * Constructs an admin with a given username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public Admin(String username, String password){
        super(username,password);
        this.reports = new ArrayList<>();
    }

    /**
     * Getting reports
     * @return the reports
     */
    public List<Report> getReports() {
        return reports;
    }

    /**
     * Setting reports
     * @param reports reports
     */
    public void setReports(List<Report> reports){
        this.reports = reports;
    }

    /**
     * if the user is frozen, admins cannot be frozen so this is always false
     * @return admins cannot be frozen
     */
    @Override
    public boolean isFrozen() {
        return false;
    }
}

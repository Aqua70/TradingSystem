package backend.tradesystem.general_managers;

import backend.exceptions.UserNotFoundException;
import backend.models.Report;
import backend.models.users.Admin;
import backend.tradesystem.Manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for managing reports
 */
public class ReportManager extends Manager {


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public ReportManager() throws IOException {
        super();
    }



    /**
     * Gets all reports
     * Each element in the list is structured like such: [fromUserId, reportedUserId, message, reportId]
     *
     * @return all reports
     */
    public List<String[]> getReports() {
        for (String userId : getAllUsers()) {
            try {
                if (getUser(userId) instanceof Admin) {
                    Admin admin = ((Admin) getUser(userId));
                    List<String[]> reports = new ArrayList<>();
                    for (Report report : admin.getReports()) {
                        String[] item = {report.getFromUserId(), report.getReportOnUserId(), report.getMessage(), report.getId()};
                        reports.add(item);
                    }
                    return reports;
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Remove a report from the list of reports
     *
     * @param reportId the report being removed
     */
    public void clearReport(String reportId) {
        for (String userId : getAllUsers()) {
            try {
                if (getUser(userId) instanceof Admin) {
                    Admin admin = ((Admin) getUser(userId));
                    List<Report> reports = admin.getReports();
                    reports.removeIf(report -> report.getId().equals(reportId));
                    updateUserDatabase(admin);
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears all reports
     */
    public void clearReports() {
        for (String userId : getAllUsers()) {
            try {
                if (getUser(userId) instanceof Admin) {
                    Admin admin = ((Admin) getUser(userId));
                    admin.setReports(new ArrayList<>());
                    updateUserDatabase(admin);
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}

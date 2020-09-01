package frontend.components;

import java.io.IOException;

import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.UserQuery;

/**
 * Used for displaying a trader in a JComboBox
 */
public class TraderComboBoxItem {
    private final UserQuery userQuery = new UserQuery();
    
        final String id;

    /**
     * Trader id
     * @param id id of the trader
     * @throws IOException if issues with getting the database file
     */
    public TraderComboBoxItem(String id) throws IOException {
            this.id = id;
        }

    /**
     * Gets the username of the trader
     * @return the username of the trader
     */
    @Override
        public String toString() {
            try {
                return userQuery.getUsername(id);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

    /**
     * The id of the trader
     * @return id of the trader
     */
    public String getId() {
            return id;
        }
    
}
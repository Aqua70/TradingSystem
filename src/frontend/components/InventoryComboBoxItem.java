package frontend.components;

import java.io.IOException;

import backend.exceptions.TradableItemNotFoundException;
import backend.tradesystem.queries.ItemQuery;

/**
 * Used for displaying an item in a JComboBox
 */
public class InventoryComboBoxItem {
    final String id;
    private final ItemQuery itemQuery = new ItemQuery();

    /**
     * Making a new item
     * @param id the id of the tradable item
     * @throws IOException issues with database file
     */
    public InventoryComboBoxItem(String id) throws IOException {
        this.id = id;
    }

    /**
     * String representation
     * @return name of the item
     */
    @Override
    public String toString() {
        try {
            return itemQuery.getName(id);
        } catch (TradableItemNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Id of the item
     * @return id of the item
     */
    public String getId() {
        return id;
    }
    
}
package backend.models;


import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an item that is supposed to be traded
 */
public class TradableItem  implements Serializable, Idable{

    private final String NAME;
    private final String DESCRIPTION;
    private final String id = UUID.randomUUID().toString();
    /**
     * Constructs a tradable item.
     *
     * @param name        The name of the TradableItem
     * @param description The description of the TradableItem
     */
    public TradableItem(String name, String description) {
        this.NAME = name;
        this.DESCRIPTION = description;
    }

    /**
     * name of the item
     *
     * @return name of the item
     */
    public String getName() {
        return NAME;
    }

    /**
     * description of the item
     *
     * @return description of the item
     */
    public String getDesc() {
        return DESCRIPTION;
    }

    /**
     * name of the item
     * @return name of the item
     */
    @Override
    public String toString(){
        return NAME;
    }

    /**
     * Gets the id
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }
}

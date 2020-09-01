package backend.exceptions;

/**
 * When the tradable item entry is not found
 */
public class TradableItemNotFoundException extends EntryNotFoundException {
    /**
     * Making an exception when the tradable item id entry isn't found
     *
     * @param id the tradable item id that wasn't found
     */
    public TradableItemNotFoundException(String id) {
        super(id, "The item " + id + " was not found.");
    }

    /**
     * Exception with no msg
     */
    public TradableItemNotFoundException(){
        super();
    }
}
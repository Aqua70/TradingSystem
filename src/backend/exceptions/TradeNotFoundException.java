package backend.exceptions;

/**
 * The trade entry doesn't exist
 */
public class TradeNotFoundException extends EntryNotFoundException {
    /**
     * Making an exception when the trade id entry isn't found
     * @param id the trade id that wasn't found
     */
    public TradeNotFoundException(String id){
        super(id, "The trade " + id + " was not found.");
    }
}
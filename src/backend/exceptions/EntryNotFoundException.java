package backend.exceptions;

/**
 * The entry was not found in the database
 */
public class EntryNotFoundException extends Exception {
    private String entryId;

    /**
     * Makes a new exception
     *
     * @param id the id of the entry
     */
    public EntryNotFoundException(String id) {
        super("The entry " + id + " was not found.");
        this.entryId = id;
    }

    /**
     * Exception with id and message
     * @param id the id being checked
     * @param msg the error message
     */
    public EntryNotFoundException(String id, String msg){
        super(msg);
        this.entryId = id;
    }

    /**
     * No error message exception
     */
    public EntryNotFoundException(){
        super();
    }

    /**
     * @return the id of the entry
     */
    public String getId() {
        return entryId;
    }
}
package backend.exceptions;

/**
 * The user entry doesn't exist
 */
public class UserNotFoundException extends EntryNotFoundException {
    /**
     * Making an exception when the user id entry isn't found
     * @param id the user id that wasn't found
     */
    public UserNotFoundException(String id){
        super(id, "The user " + id + " was not found.");
    }

    /**
     * No error message exception
     */
    public UserNotFoundException(){
        super();
    }
}
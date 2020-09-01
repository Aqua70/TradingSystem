package backend.exceptions;

/**
 * The user entry already exists
 */
public class UserAlreadyExistsException extends Exception {
    /**
     * no message exception
     */
    public UserAlreadyExistsException(){
        super();
    }
}
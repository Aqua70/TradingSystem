package backend.exceptions;

/**
 * If the password isn't valid
 */
public class BadPasswordException extends Exception{
    /**
     * Bad password
     * @param msg the message
     */
    public BadPasswordException(String msg){
        super(msg);
    }
}

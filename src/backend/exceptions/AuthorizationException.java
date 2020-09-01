package backend.exceptions;


/**
 * If a user doesn't have authority
 */
public class AuthorizationException extends Exception {
    /**
     * New exception without err message
     */
    public AuthorizationException(){super();}

    /**
     * New exception with err message
     * @param msg the err message
     */
    public AuthorizationException(String msg){super(msg);}
}

package backend.exceptions;

/**
 * Refers to everything about how purchase cannot happen
 */
public class CannotPurchaseException extends Exception {
    /**
     * New exception without err message
     */
    public CannotPurchaseException(){super();}

    /**
     * New exception with err message
     * @param msg the err message
     */
    public CannotPurchaseException(String msg){super(msg);}
}

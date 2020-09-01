package backend.exceptions;

/**
 * Refers to everything about how trading cannot happen
 */
public class CannotTradeException extends Exception {
    /**
     * New exception without err message
     */
    public CannotTradeException(){super();}

    /**
     * New exception with err message
     * @param msg the err message
     */
    public CannotTradeException(String msg){super(msg);}
}

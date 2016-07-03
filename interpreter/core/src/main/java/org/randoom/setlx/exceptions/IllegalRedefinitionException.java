package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user redefines something which is not allowed to be redefined.
 */
public class IllegalRedefinitionException extends CatchableInSetlXException {

    private static final long serialVersionUID = -8062903832197478456L;

    /**
     * Create new IllegalRedefinitionException.
     *
     * @param msg Message describing the exception that occurred.
     */
    public IllegalRedefinitionException(final String msg) {
        super(msg);
    }

    /**
     * Create new IllegalRedefinitionException.
     *
     * @param msg   Message describing the exception that occurred.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    public IllegalRedefinitionException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}


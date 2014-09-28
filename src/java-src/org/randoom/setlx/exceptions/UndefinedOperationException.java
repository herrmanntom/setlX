package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user wants to execute an undefined operation.
 */
public class UndefinedOperationException extends CatchableInSetlXException {

    private static final long serialVersionUID = -6219122895156982773L;

    /**
     * Create new UndefinedOperationException.
     *
     * @param msg More detailed message.
     */
    public UndefinedOperationException(final String msg) {
        super(msg);
    }

    /**
     * Create a new UndefinedOperationException.
     *
     * @param msg   Message describing the exception that occurred.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    public UndefinedOperationException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}


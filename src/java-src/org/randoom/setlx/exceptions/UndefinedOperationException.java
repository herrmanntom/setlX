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
}


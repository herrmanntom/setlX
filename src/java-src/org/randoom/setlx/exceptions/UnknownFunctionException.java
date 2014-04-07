package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user wants to execute an undefined function.
 */
public class UnknownFunctionException extends CatchableInSetlXException {

    private static final long serialVersionUID = -1463034070698791399L;

    /**
     * Create new UnknownFunctionException.
     *
     * @param msg More detailed message.
     */
    public UnknownFunctionException(final String msg) {
        super(msg);
    }
}


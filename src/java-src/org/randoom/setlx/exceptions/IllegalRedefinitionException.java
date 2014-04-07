package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user redefines something which is not allowed to be redefined.
 */
public class IllegalRedefinitionException extends CatchableInSetlXException {

    private static final long serialVersionUID = -8062903832197478456L;

    /**
     * Create new IllegalRedefinitionException.
     *
     * @param msg More detailed error message.
     */
    public IllegalRedefinitionException(final String msg) {
        super(msg);
    }
}


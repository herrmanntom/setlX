package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user tries to use a number that is expected to be an integer, but is not.
 */
public class NotAnIntegerException extends CatchableInSetlXException {

    private static final long serialVersionUID = 302358553843600634L;

    /**
     * Create a new NotAnIntegerException.
     *
     * @param msg More detailed message.
     */
    public NotAnIntegerException(final String msg) {
        super(msg);
    }
}


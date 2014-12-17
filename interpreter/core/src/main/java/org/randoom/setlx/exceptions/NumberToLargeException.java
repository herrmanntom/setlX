package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user tries to use a number that is out of the expected range.
 */
public class NumberToLargeException extends CatchableInSetlXException {

    private static final long serialVersionUID = -1696395522719738998L;

    /**
     * Create a NumberToLargeException.
     *
     * @param msg More detailed message.
     */
    public NumberToLargeException(final String msg) {
        super(msg);
    }
}


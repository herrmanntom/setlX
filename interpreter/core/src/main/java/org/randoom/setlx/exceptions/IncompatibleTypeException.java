package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user supplies parameters of an incorrect type.
 */
public class IncompatibleTypeException extends CatchableInSetlXException {

    private static final long serialVersionUID = 1405145935855945511L;

    /**
     * Create new IncompatibleTypeException.
     *
     * @param msg More detailed message.
     */
    public IncompatibleTypeException(final String msg) {
        super(msg);
    }
}


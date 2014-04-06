package org.randoom.setlx.exceptions;

/**
 * Exceptions inheriting from this class are supposed to be catchable by SetlX's
 * own try-catch block.
 */
public abstract class CatchableInSetlXException extends SetlException {

    private static final long serialVersionUID = 6648512220565994302L;

    /**
     * Create a new CatchableInSetlXException.
     *
     * @param msg Message describing the exception that occurred.
     */
    public CatchableInSetlXException(final String msg) {
        super(msg);
    }
}


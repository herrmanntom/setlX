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
    protected CatchableInSetlXException(final String msg) {
        super(msg);
    }

    /**
     * Create a new CatchableInSetlXException.
     *
     * @param msg   Message describing the exception that occurred.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    protected CatchableInSetlXException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}


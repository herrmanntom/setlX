package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user tries to convert an incorrectly formed term into a CodeFragment.
 */
public class TermConversionException extends SetlException {

    private static final long serialVersionUID = -6557668501097576932L;

    /**
     * Create new TermConversionException.
     *
     * @param msg More detailed message.
     */
    public TermConversionException(final String msg) {
        super(msg);
    }

    /**
     * Create a new TermConversionException.
     *
     * @param msg   More detailed message.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    public TermConversionException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}


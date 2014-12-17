package org.randoom.setlx.exceptions;

/**
 * User-generated Exception, thrown by the assert statement.
 */
public class AssertException extends SetlException {

    private static final long serialVersionUID = -7617079806429159395L;

    /**
     * Create a new AssertException.
     *
     * @param msg Message describing the exception that occurred.
     */
    public AssertException(final String msg) {
        super(msg);
    }
}


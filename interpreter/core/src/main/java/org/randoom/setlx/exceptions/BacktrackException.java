package org.randoom.setlx.exceptions;

/**
 * User-generated Exception, thrown by the backtrack statement.
 */
public class BacktrackException extends SetlException {

    private static final long serialVersionUID = -5672153454789983452L;

    /**
     * Create a new BacktrackException.
     */
    public BacktrackException() {
        super("Backtrack-statement was executed outside of check-statement.");
    }
}


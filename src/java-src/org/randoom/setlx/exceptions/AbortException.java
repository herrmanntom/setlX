package org.randoom.setlx.exceptions;

/**
 * User-generated Exception, thrown by the abort function.
 */
public class AbortException extends CatchableInSetlXException {

    private static final long serialVersionUID = -364970806037676982L;

    /**
     * Create a new AbortException.
     *
     * @param msg Message describing the exception that occurred.
     */
    public AbortException(final String msg) {
        super(msg);
    }
}


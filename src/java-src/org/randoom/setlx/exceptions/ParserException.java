package org.randoom.setlx.exceptions;

/**
 * Base class for Exceptions thrown when parsing incorrect code.
 */
public abstract class ParserException extends CatchableInSetlXException {

    private static final long serialVersionUID = 6459102607830969706L;

    /**
     * Create new ParserException.
     *
     * @param msg More detailed error message.
     */
    public ParserException(final String msg) {
        super(msg);
    }
}


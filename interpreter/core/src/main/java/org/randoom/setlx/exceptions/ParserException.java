package org.randoom.setlx.exceptions;

/**
 * Base class for Exceptions thrown when parsing incorrect code.
 */
public abstract class ParserException extends CatchableInSetlXException {

    private static final long serialVersionUID = 6459102607830969706L;

    /**
     * Create new ParserException.
     *
     * @param msg   More detailed error message.
     * @param cause Exception thrown by the JVM.
     */
    protected ParserException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}


package org.randoom.setlx.exceptions;

/**
 * Exception, thrown when encountering the end of a file.
 */
public class EndOfFileException extends ParserException {

    private static final long serialVersionUID = 3815004673468458510L;

    /**
     * Create new EndOfFileException exception.
     */
    public EndOfFileException() {
        this("EndOfFile", null);
    }

    /**
     * Create new EndOfFileException exception.
     *
     * @param cause Exception thrown by the JVM.
     */
    public EndOfFileException(final Throwable cause) {
        this("EndOfFile", cause);
    }

    /**
     * Create new EndOfFileException.
     *
     * @param msg More detailed message.
     * @param cause Exception thrown by the JVM.
     */
    public EndOfFileException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}


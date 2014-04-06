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
        super("EndOfFile");
    }

    /**
     * Create new EndOfFileException.
     *
     * @param msg More detailed message.
     */
    public EndOfFileException(final String msg) {
        super(msg);
    }
}


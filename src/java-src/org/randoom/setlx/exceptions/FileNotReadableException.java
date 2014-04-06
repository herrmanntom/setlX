package org.randoom.setlx.exceptions;

/**
 * Exception, thrown when trying to read an unavailable file.
 */
public class FileNotReadableException extends ParserException {

    private static final long serialVersionUID = -1285528014354899930L;

    /**
     * Create a new FileNotReadableException.
     *
     * @param msg More detailed message.
     */
    public FileNotReadableException(final String msg) {
        super(msg);
    }
}


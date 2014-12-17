package org.randoom.setlx.exceptions;

/**
 * Exception, thrown when trying to read an unavailable file.
 */
public class FileNotReadableException extends ParserException {

    private static final long serialVersionUID = -1285528014354899930L;

    /**
     * Create a new FileNotReadableException.
     *
     * @param msg   More detailed message.
     * @param cause Exception thrown by the JVM.
     */
    public FileNotReadableException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}


package org.randoom.setlx.exceptions;

/**
 * Exception, thrown when trying to write an unwritable file.
 */
public class FileNotWritableException extends SetlException {

    private static final long serialVersionUID = 8898977595418622652L;

    /**
     * Create a new FileNotWritableException.
     *
     * @param msg More detailed message.
     */
    public FileNotWritableException(final String msg) {
        super(msg);
    }
}


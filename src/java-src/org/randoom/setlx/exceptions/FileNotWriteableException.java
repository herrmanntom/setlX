package org.randoom.setlx.exceptions;

/**
 * Exception, thrown when trying to write an unwritable file.
 */
public class FileNotWriteableException extends SetlException {

    private static final long serialVersionUID = 8898977595418622652L;

    /**
     * Create a new FileNotWriteableException.
     *
     * @param msg More detailed message.
     */
    public FileNotWriteableException(final String msg) {
        super(msg);
    }
}


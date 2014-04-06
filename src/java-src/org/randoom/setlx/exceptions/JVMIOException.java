package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the JVM has thrown an unexpected IO Exception.
 */
public class JVMIOException extends SetlException {

    private static final long serialVersionUID = 557883382696423828L;

    /**
     * Create new JVMIOException.
     *
     * @param msg More detailed message.
     */
    public JVMIOException(final String msg) {
        super(msg);
    }
}


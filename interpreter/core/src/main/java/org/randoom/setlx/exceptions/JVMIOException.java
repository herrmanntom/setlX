package org.randoom.setlx.exceptions;

import java.io.IOException;

/**
 * Exception thrown, when the JVM has thrown an unexpected IO Exception.
 */
public class JVMIOException extends JVMException {

    private static final long serialVersionUID = 557883382696423828L;

    /**
     * Create new JVMIOException.
     *
     * @param msg   User friendly message.
     * @param cause Exception thrown by the JVM.
     */
    public JVMIOException(final String msg, final IOException cause) {
        super(msg, cause);
    }
}


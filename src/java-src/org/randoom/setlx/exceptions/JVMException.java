package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the JVM has thrown one unexpectedly.
 */
public class JVMException extends SetlException {

    private static final long serialVersionUID = 7272703507913719794L;

    /**
     * Create new JVMException.
     *
     * @param msg   More detailed message.
     * @param cause Exception thrown by the JVM.
     */
    public JVMException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}


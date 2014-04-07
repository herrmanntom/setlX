package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the interpreter is terminated prematurely by the user.
 */
public class StopExecutionException extends SetlException {

    private static final long serialVersionUID = 1272703503259932794L;

    /**
     * Create a new StopExecutionException.
     */
    public StopExecutionException() {
        super("Interrupted");
    }
}


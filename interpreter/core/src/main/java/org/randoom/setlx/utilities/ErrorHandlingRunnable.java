package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;

/**
 * Base class for all Runnables in setlX, that should handle errors.
 * Mainly used to control naming and stack size.
 */
public abstract class ErrorHandlingRunnable extends BaseRunnable {
    private       Throwable error;

    /**
     * Initialize this new BaseRunnable
     * @param state     Current state of the running setlX program.
     * @param stackSize Set preferred stack size for this thread.
     */
    protected ErrorHandlingRunnable(State state, StackSize stackSize) {
        super(state, stackSize);
        this.error     = null;
    }

    /**
     * Start this runnable as a thread and return after that thread finishes.
     * Rethrows all exceptions thrown in that thread.
     *
     * @throws org.randoom.setlx.exceptions.SetlException when this exception was thrown in started thread.
     */
    public void startAsThread() throws SetlException {
        try {
            // prevent running out of stack by creating a new thread
            Thread thread = createThread();
            thread.start();
            thread.join();

            // handle exceptions thrown in thread
            if (error != null) {
                if (error instanceof SetlException) {
                    throw (SetlException) error;
                } else if (error instanceof StackOverflowError) {
                    throw (StackOverflowError) error;
                } else if (error instanceof OutOfMemoryError) {
                    throw (OutOfMemoryError) error;
                } else if (error instanceof RuntimeException) {
                    throw (RuntimeException) error;
                }
            }
        } catch (final InterruptedException e) {
            throw new StopExecutionException();
        }
    }

    @Override
    public final void run() {
        try {
            exec(state);
        } catch (final SetlException se) {
            error = se;
        } catch (final StackOverflowError soe) {
            error = soe;
        } catch (final OutOfMemoryError oome) {
            error = oome;
        } catch (final RuntimeException re) {
            error = re;
        }
    }

    /**
     * Statements to execute
     *
     * @param state          Current state of the running setlX program.
     * @throws org.randoom.setlx.exceptions.SetlException in case of (user-) error.
     */
    public abstract void exec(State state) throws SetlException;
}

package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;

/**
 * Base class for all Runnables in setlX.
 * Mainly used to control naming and stack size.
 */
public abstract class BaseRunnable implements Runnable {
    private final State     state;
    private final StackSize stackSize;
    private       Throwable error;

    /**
     * Select how much Stack this thread should request
     */
    public static enum StackSize {
        /** request large stack size */
        LARGE,
        /** request medium stack size */
        MEDIUM,
        /** request small stack size */
        SMALL
    }

    /**
     * Initialize this new BaseRunnable
     * @param state     Current state of the running setlX program.
     * @param stackSize Set preferred stack size for this thread.
     */
    protected BaseRunnable(State state, StackSize stackSize) {
        this.state     = state;
        this.stackSize = stackSize;
        this.error     = null;
    }

    /**
     * Create a new thread for this runnable using the thread name and stack size hint.
     *
     * @return new Thread.
     */
    public Thread createThread() {
        EnvironmentProvider environmentProvider = state.getEnvironmentProvider();
        int size;
        if (stackSize == StackSize.SMALL) {
            size = environmentProvider.getSmallStackSizeWishInKb();
        } else if (stackSize == StackSize.MEDIUM) {
            size = environmentProvider.getMediumStackSizeWishInKb();
        } else {
            size = environmentProvider.getStackSizeWishInKb();
        }
        Thread currentThread = Thread.currentThread();
        return new Thread(
                currentThread.getThreadGroup(),
                this,
                currentThread.getName() + "::" + getThreadName(),
                size * 1024
        );
    }

    /**
     * Start this runnable as a thread and return after that thread finishes.
     * Rethrows all exceptions thrown in that thread.
     *
     * @throws SetlException when this exception was thrown in started thread.
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
     * @throws SetlException in case of (user-) error.
     */
    public abstract void exec(State state) throws SetlException;

    /**
     * Get name suffix of the thread to create
     *
     * @return name suffix
     */
    public abstract String getThreadName();
}

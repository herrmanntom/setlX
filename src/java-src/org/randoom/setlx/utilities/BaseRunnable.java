package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all Runnables in setlX.
 * Mainly used to control naming and stack size.
 */
public abstract class BaseRunnable implements Runnable {
    private final static AtomicInteger count = new AtomicInteger(0);

    private final State     state;
    private final boolean   smallStackSize;
    private       Throwable error;

    /**
     * Initialize this new BaseRunnable
     * @param state          Current state of the running setlX program.
     * @param smallStackSize Set low stack size for this thread.
     */
    protected BaseRunnable(State state, boolean smallStackSize) {
        this.state          = state;
        this.smallStackSize = smallStackSize;
        this.error         = null;
    }

    /**
     * Create a new thread for this runnable using the thread name and stack size hint.
     *
     * @return new Thread.
     */
    public Thread createThread() {
        EnvironmentProvider environmentProvider = state.getEnvironmentProvider();
        if (count.getAndIncrement() >= environmentProvider.getMaximumNumberOfThreads()) {
            throw new StackOverflowError("Out of stack replacement threads.");
        }
        int stackSize;
        if (smallStackSize) {
            stackSize = Math.min(64, environmentProvider.getStackSizeWishInKb());
        } else {
            stackSize = environmentProvider.getStackSizeWishInKb();
        }
        Thread currentThread = Thread.currentThread();
        return new Thread(
                currentThread.getThreadGroup(),
                this,
                currentThread.getName() + "::" + getThreadName(),
                stackSize * 1024
        );
    }

    public void startAsThread() throws SetlException {
        // store and increase callStackDepth
        final int oldCallStackDepth = state.callStackDepth;

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
                    state.storeStackDepthOfFirstCall(state.callStackDepth);
                    throw (StackOverflowError) error;
                } else if (error instanceof OutOfMemoryError) {
                    throw (OutOfMemoryError) error;
                } else if (error instanceof RuntimeException) {
                    throw (RuntimeException) error;
                }
            }
        } catch (final InterruptedException e) {
            throw new StopExecutionException();
        } finally {
            // reset callStackDepth
            state.callStackDepth = oldCallStackDepth;
        }
    }

    @Override
    public final void run() {
        try {
            state.callStackDepth  = 0;
            exec(state);
        } catch (final SetlException se) {
            error = se;
        } catch (final StackOverflowError soe) {
            error = soe;
        } catch (final OutOfMemoryError oome) {
            error = oome;
        } catch (final RuntimeException re) {
            error = re;
        } finally {
            count.getAndDecrement();
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

package org.randoom.setlx.utilities;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all Runnables in setlX.
 * Mainly used to control naming and stack size.
 */
public abstract class BaseRunnable implements Runnable {
    private final static AtomicInteger count = new AtomicInteger(0);

    /**
     * Create a new thread for this runnable using the thread name and stack size hint.
     *
     * @param state          Current state of the running setlX program.
     * @param smallStackSize Set low stack size for this thread.
     * @return new Thread.
     */
    public Thread createThread(State state, boolean smallStackSize) {
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

    @Override
    public final void run() {
        try {
            exec();
        } finally {
            count.getAndDecrement();
        }
    }

    /**
     *  Statements to execute
     */
    public abstract void exec();

    /**
     * Get name suffix of the thread to create
     *
     * @return name suffix
     */
    public abstract String getThreadName();
}

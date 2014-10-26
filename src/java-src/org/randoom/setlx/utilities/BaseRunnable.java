package org.randoom.setlx.utilities;

/**
 * Base class for all Runnables in setlX.
 * Mainly used to control naming and stack size.
 */
public abstract class BaseRunnable implements Runnable {
    /**
     * Create a new thread for this runnable using the thread name and stack size hint.
     *
     * @return new Thread.
     */
    public Thread createThread() {
        Thread currentThread = Thread.currentThread();
        return new Thread(
                currentThread.getThreadGroup(),
                this,
                currentThread.getName() + "::" + getThreadName(),
                2 * 1024 * 1024 // 2 megabyte
        );
    }

    public abstract String getThreadName();
}

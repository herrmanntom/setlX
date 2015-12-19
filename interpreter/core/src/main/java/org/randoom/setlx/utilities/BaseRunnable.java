package org.randoom.setlx.utilities;

/**
 * Base class for all Runnables in setlX.
 * Mainly used to control naming and stack size.
 */
public abstract class BaseRunnable implements Runnable {
    /** Current state of the running setlX program. */
    protected       State     state;
    private   final StackSize stackSize;

    /**
     * Select how much Stack this thread should request
     */
    public enum StackSize {
        /** request large stack size */
        LARGE,
        /** request medium stack size */
        MEDIUM,
        /** request small stack size */
        SMALL;

        private String threadNamePrefix;

        StackSize() {
            threadNamePrefix = ":" + this.name();
        }

        /**
         * Get stack size prefix added to thread name when created with this stack size.
         *
         * @return Stack size prefix added to thread name.
         */
        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }
    }

    /**
     * Initialize this new BaseRunnable
     * @param state     Current state of the running setlX program.
     * @param stackSize Set preferred stack size for this thread.
     */
    protected BaseRunnable(State state, StackSize stackSize) {
        this.state     = state;
        this.stackSize = stackSize;
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
                currentThread.getName() + "::" + getThreadName() + stackSize.getThreadNamePrefix(),
                size * 1024
        );
    }

    /**
     * Get name suffix of the thread to create
     *
     * @return name suffix
     */
    public abstract String getThreadName();
}

package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.*;

/**
 * Base class for all SetlX statement.
 */
public abstract class Statement extends ImmutableCodeFragment {
    /**
     * Code returned by executeWithErrorHandling().
     */
    public enum EXECUTE {
        /**
         * Code returned by executeWithErrorHandling() when the execution stopped
         * without any error occurring or the user explicitly exiting.
         *
         * @see org.randoom.setlx.statements.Statement#executeWithErrorHandling(State, boolean)
         */
        OK,
        /**
         * Code returned by executeWithErrorHandling() when the execution stopped
         * after some kind of error.
         *
         * @see org.randoom.setlx.statements.Statement#executeWithErrorHandling(State, boolean)
         */
        ERROR,
        /**
         * Code returned by executeWithErrorHandling() when the user used the exit statement.
         *
         * @see org.randoom.setlx.statements.Statement#executeWithErrorHandling(State, boolean)
         */
        EXIT
    }

    /**
     * Execute-method to be implemented by classes representing actual statement.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the execution (e.g. return value, continue, etc).
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract ReturnMessage execute(final State state) throws SetlException;

    /**
     * Execute this statement and handle all exceptions.
     *
     * @param state             Current state of the running setlX program.
     * @param hintAtJVMxOptions Print hints to -X?? JVM options to get around certain errors.
     * @return                  Coded result type. (see EXECUTE_? constants)
     */
    public EXECUTE executeWithErrorHandling(final State state, final boolean hintAtJVMxOptions) {
        try {
            new StatementRunner(this, state).startAsThread();

            return EXECUTE.OK;

        } catch (final AbortException ae) { // code detected user did something wrong
            state.errWriteLn(ae.getMessage());
            return EXECUTE.ERROR;

        } catch (final ExitException ee) { // user/code wants to quit
            if (state.isInteractive()) {
                state.outWriteLn(ee.getMessage());
            }

            return EXECUTE.EXIT;

        } catch (final SetlException se) { // user/code did something wrong
            se.printExceptionsTrace(state, 40);
            return EXECUTE.ERROR;

        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);

            state.errWriteOutOfStack(soe, false);
            return EXECUTE.ERROR;

        } catch (final OutOfMemoryError oome) {
            try {
                // free some memory
                state.resetState();
                // give hint to the garbage collector
                Runtime.getRuntime().gc();
                // sleep a while
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                /* don't care any more */
            }

            state.errWriteOutOfMemory(hintAtJVMxOptions, false);
            return EXECUTE.ERROR;

        } catch (final Exception e) { // this should never happen...
            state.errWriteInternalError(e);
            return EXECUTE.ERROR;
        }
    }

    /**
     * Subclass to cheat the end of the world... or stack, whatever comes first.
     */
    protected class StatementRunner extends BaseRunnable {
        private final Statement     statement;
        private       ReturnMessage result;

        /**
         * Create a new StatementRunner.
         *
         * @param statement Statement to execute.
         * @param state     Current state of the running setlX program.
         */
        /*package*/ StatementRunner(final Statement statement, final State state) {
            super(state, false);
            this.statement = statement;
            this.result     = null;
        }

        @Override
        public void exec(State state) throws SetlException {
            result = statement.execute(state);
        }

        @Override
        public String getThreadName() {
            return statement.getClass().getSimpleName();
        }

        /**
         * Get result of the execution
         *
         * @return result of the execution
         */
        public ReturnMessage getResult() {
            return result;
        }
    }
}


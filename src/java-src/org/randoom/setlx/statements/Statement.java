package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

/**
 * Base class for all SetlX statements.
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
     * Execute-method to be implemented by classes representing actual statements.
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
            // increase callStackDepth
            ++(state.callStackDepth);

            execute(state);
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
        } finally {
            // decrease callStackDepth
            --(state.callStackDepth);
        }
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

/**
 * Base class for all SetlX statements.
 */
public abstract class Statement extends CodeFragment {

    //TODO: use enum
    /**
     * Code returned by executeWithErrorHandling() when the execution stopped
     * without any error occurring or the user explicitly exiting.
     *
     * @see org.randoom.setlx.statements.Statement#executeWithErrorHandling(State, boolean)
     */
    public final static int EXECUTE_OK    = 23;
    /**
     * Code returned by executeWithErrorHandling() when the execution stopped
     * after some kind of error.
     *
     * @see org.randoom.setlx.statements.Statement#executeWithErrorHandling(State, boolean)
     */
    public final static int EXECUTE_ERROR = 33;
    /**
     * Code returned by executeWithErrorHandling() when the user used the exit statement.
     *
     * @see org.randoom.setlx.statements.Statement#executeWithErrorHandling(State, boolean)
     */
    public final static int EXECUTE_EXIT  = 42;

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
    public int executeWithErrorHandling(final State state, final boolean hintAtJVMxOptions) {
        try {
            // increase callStackDepth
            ++(state.callStackDepth);

            execute(state);
            return EXECUTE_OK;

        } catch (final AbortException ae) { // code detected user did something wrong
            state.errWriteLn(ae.getMessage());
            return EXECUTE_ERROR;

        } catch (final ExitException ee) { // user/code wants to quit
            if (state.isInteractive()) {
                state.outWriteLn(ee.getMessage());
            }

            return EXECUTE_EXIT;

        } catch (final SetlException se) { // user/code did something wrong
            se.printExceptionsTrace(state, 40);
            return EXECUTE_ERROR;

        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);

            state.errWriteOutOfStack(soe, false);
            return EXECUTE_ERROR;

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
            return EXECUTE_ERROR;

        } catch (final Exception e) { // this should never happen...
            state.errWriteInternalError(e);
            return EXECUTE_ERROR;
        } finally {
            // decrease callStackDepth
            --(state.callStackDepth);
        }
    }

}


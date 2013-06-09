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

    public final static int EXECUTE_OK    = 23;
    public final static int EXECUTE_ERROR = 33;
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
            state.errWriteOutOfStack(soe, false);
            return EXECUTE_ERROR;

        } catch (final OutOfMemoryError oome) {
            state.errWriteOutOfMemory(hintAtJVMxOptions, false);
            return EXECUTE_ERROR;

        } catch (final Exception e) { // this should never happen...
            state.errWriteInternalError(e);
            return EXECUTE_ERROR;
        }
    }

}


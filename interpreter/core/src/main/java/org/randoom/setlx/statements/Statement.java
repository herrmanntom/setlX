package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ErrorHandlingRunnable;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all SetlX statement.
 */
public abstract class Statement extends ImmutableCodeFragment {
    private final static Map<String, Method> STATEMENT_CONVERTERS = new HashMap<String, Method>();
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
            se.printExceptionsTrace(state);
            return EXECUTE.ERROR;

        } catch (final StackOverflowError soe) {
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
     * Create a Statement from a term representing a statement
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param functionalCharacter      Functional character of the term to convert.
     * @return                         New Statement or null, if term does not represent a statement.
     * @throws TermConversionException in case the term is malformed.
     */
    public static Statement createFromTerm(State state, Term term, String functionalCharacter) throws TermConversionException {
        Method converter;
        synchronized (STATEMENT_CONVERTERS) {
            converter = STATEMENT_CONVERTERS.get(functionalCharacter);
        }
        // search via reflection, if method was not found in map
        if (converter == null) {
            Class<? extends Statement> statementClass = TermUtilities.getClassForTerm(Statement.class, functionalCharacter);

            if (statementClass != null) {
                try {
                    converter = statementClass.getMethod("termToStatement", State.class, Term.class);

                    synchronized (STATEMENT_CONVERTERS) {
                        STATEMENT_CONVERTERS.put(functionalCharacter, converter);
                    }
                } catch (final Exception e1) {
                    // look-up failed
                }
            }
        }
        // invoke method found
        if (converter != null) {
            try {
                return unify((Statement) converter.invoke(null, state, term));
            } catch (final Exception e) {
                //noinspection ConstantConditions
                if (e instanceof TermConversionException) {
                    throw (TermConversionException) e;
                } else { // will never happen ;-)
                    // because we know this method exists etc
                    throw new TermConversionException("Impossible error...");
                }
            }
        }
        return null;
    }

    /**
     * Subclass to cheat the end of the world... or stack, whatever comes first.
     */
    private static final class StatementRunner extends ErrorHandlingRunnable {
        private final Statement     statement;
        private       ReturnMessage result;

        /**
         * Create a new StatementRunner.
         *
         * @param statement Statement to execute.
         * @param state     Current state of the running setlX program.
         */
        private StatementRunner(final Statement statement, final State state) {
            super(state, StackSize.LARGE);
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


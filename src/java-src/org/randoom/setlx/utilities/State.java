package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.LinkedList;
import java.util.Random;

/**
 * This interface provides access to the current state of the interpreter.
 */
public abstract class State {

    public abstract void                setEnvironmentProvider(final EnvironmentProvider envProvider);

    public abstract EnvironmentProvider getEnvironmentProvider();

    /* -- I/O -- */

    public abstract String              inReadLine() throws JVMIOException;

    // write to standard output
    public abstract void                outWrite(final String msg);
    public abstract void                outWriteLn(final String msg);
    public abstract void                outWriteLn();

    // write to standard error
    public abstract void                errWrite(final String msg);
    public abstract void                errWriteLn(final String msg);
    public abstract void                errWriteLn();

    // capture/write parser errors
    public abstract void                writeParserErrLn(final String msg);
    public abstract LinkedList<String>  getParserErrorCapture();
    public abstract void                setParserErrorCapture(final LinkedList<String> capture);
    // our own error accounting, which survives nested parsing
    public abstract int                 getParserErrorCount();
    public abstract void                addToParserErrorCount(int numberOfErrors);
    public abstract void                resetParserErrorCount();

    // some special (error) messages
    public abstract void                errWriteInternalError(final Exception e);
    public abstract void                errWriteOutOfStack(final StackOverflowError soe, boolean fromParser);
    public abstract void                errWriteOutOfMemory(final boolean showXmxOption, boolean fromParser);

    /**
     * Show the user a prompt for some input. The prompt message is only shown if
     * input comes from a human, i.e. is not instantly available.
     *
     * Actual user input has to be read via inReadLine().
     *
     * @see org.randoom.setlx.utilities.State#inReadLine()
     *
     * @param prompt          Message to show to the user.
     * @return                True if input comes from a human.
     * @throws JVMIOException Thrown in case input cannot be opened.
     */
    public abstract boolean             prompt(final String prompt) throws JVMIOException;

    public abstract void                setRealPrintMode_default();
    public abstract void                setRealPrintMode_engineering();
    public abstract void                setRealPrintMode_plain();

    public          int                 realPrintMode;

    // allow modification of fileName/path when reading files
    public abstract String              filterFileName(final String fileName);

    // allow modification of library name
    public abstract String              filterLibraryName(final String name);

    public abstract boolean             isLibraryLoaded(final String name);
    public abstract void                libraryWasLoaded(final String name);

    public abstract int                 getNumberOfCores();

    // current time in ms
    public abstract long                currentTimeMillis();

    public abstract void                setRandoomPredictable(boolean predictableRandoom);
    public abstract boolean             isRandoomPredictable();

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public abstract int                 getRandomInt(final int upperBoundary);

    // get random number (all int values are possible)
    public abstract int                 getRandomInt();

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public abstract double              getRandomDouble();

    public abstract Random              getRandom();

    public          int                 callStackDepth;
    public abstract int                 getMaxStackSize();
    public abstract void                storeFirstCallStackDepth();

    public abstract void                stopExecution(final boolean stopExecution);

    public          boolean             isExecutionStopped;

    public abstract void                setMultiLineMode(final boolean multiLineMode);

    public abstract boolean             isMultiLineEnabled();

    public abstract void                setInteractive(final boolean isInteractive);

    public abstract boolean             isInteractive();

    public abstract void                setPrintVerbose(final boolean printVerbose);

    public abstract boolean             isPrintVerbose();

    public abstract void                setTraceAssignments(final boolean traceAssignments);

    public          boolean             traceAssignments;

    public abstract void                printTrace(final String var, final Value result, final String context);

    public abstract void                setAssertsDisabled(final boolean assertsDisabled);

    public abstract boolean             areAssertsDisabled();

    // 'secret' option to print stack trace of unhandled java exceptions and use more checks
    public abstract void                setRuntimeDebugging(final boolean isRuntimeDebuggingEnabled);

    public abstract boolean             isRuntimeDebuggingEnabled();

    public abstract void                appendLineStart(final StringBuilder sb, final int tabs);

    public abstract String              getEndl();
    public abstract String              getTab();

    /* -- saved variables in current scope -- */

    public abstract VariableScope       getScope();

    public abstract void                setScope(final VariableScope newScope);

    /**
     * Reset state to its initial setup.
     *
     * Clears all scopes, class definitions, libraries and error captures.
     */
    public abstract void                resetState();

    public abstract Value               findValue(final String var) throws SetlException;

    /**
     * Store `value' for variable into current scope.
     *
     * @param var                           Variable to set.
     * @param value                         Value to set variable to
     * @param context                       Context description of the assignment for trace.
     * @throws IllegalRedefinitionException Thrown in case of redefining a class.
     */
    public abstract void                putValue(final String var, final Value value, final String context) throws IllegalRedefinitionException;

    /**
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     * Return false if linked scope contained a different value under this variable,
     * true otherwise.
     *
     * @param var            Variable to set.
     * @param value          Value to set variable to.
     * @param outerScope     Scope to up to which needs to be checked.
     * @param context        Context description of the assignment for trace.
     * @return               False if linked scope contained a different value under this variable, true otherwise.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract boolean             putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope, final String context) throws SetlException;

    /**
     * Add bindings stored in `scope' into current scope.
     * This also adds variables in outer scopes of `scope' until reaching the
     * current scope as outer scope of `scope'.
     *
     * @param scope          Scope to set variables from.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract void                putAllValues(final VariableScope scope, final String context) throws SetlException;

    public abstract void                putClassDefinition(final String var, final SetlClass classDef, final String context);

    public abstract SetlHashMap<Value>  getAllVariablesInScope();

    public abstract Term                scopeToTerm();

}


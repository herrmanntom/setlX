package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.ClassDefinition;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.LinkedList;
import java.util.Random;

// This interface provides access to the current state of the interpreter.
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

    public abstract boolean             prompt(final String prompt) throws JVMIOException;

    public abstract void                promptUnchecked(final String prompt);

    // allow modification of fileName/path when reading files
    public abstract String              filterFileName(final String fileName);

    // allow modification of library name
    public abstract String              filterLibraryName(final String name);

    public abstract boolean             isLibraryLoaded(final String name);
    public abstract void                libraryWasLoaded(final String name);

    public abstract int                 getNumberOfCores();

    // current time in ms
    public abstract long                currentTimeMillis();

    public abstract void                setPredictableRandoom();

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public abstract int                 getRandomInt(final int upperBoundary);

    // get random number (all int values are possible)
    public abstract int                 getRandomInt();

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public abstract double              getRandomDouble();

    public abstract Random              getRandom();

    public abstract void                stopExecution(final boolean stopExecution);

    public boolean                      isExecutionStopped;

    public abstract void                setMultiLineMode(final boolean multiLineMode);

    public abstract boolean             isMultiLineEnabled();

    public abstract void                setInteractive(final boolean isInteractive);

    public abstract boolean             isInteractive();

    public abstract void                setPrintVerbose(final boolean printVerbose);

    public abstract boolean             isPrintVerbose();

    public abstract void                setTraceAssignments(final boolean traceAssignments);

    public boolean                      traceAssignments;

    public abstract void                setAssertsDisabled(final boolean assertsDisabled);

    public abstract boolean             areAssertsDisabled();

    // 'secret' option to print stack trace of unhandled java exceptions
    public abstract void                setUnhideExceptions(final boolean unhideExceptions);

    public abstract boolean             unhideExceptions();

    public abstract void                getLineStart(final StringBuilder sb, final int tabs);

    public abstract String              getEndl();
    public abstract String              getTab();

    /* -- saved variables in current scope -- */

    public abstract VariableScope       getScope();

    public abstract void                setScope(final VariableScope newScope);

    public abstract void                resetState();

    public abstract Value               findValue(final String var) throws SetlException;

    public abstract void                putValue(final String var, final Value value) throws IllegalRedefinitionException;

    /*
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     * Return false if linked scope contained a different value under this variable,
     * true otherwise.
     */
    public abstract boolean             putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope) throws SetlException;

    // Add bindings stored in `scope' into current scope.
    // This also adds vars in outer scopes of `scope' until reaching the current
    // scope as outer scope of `scope'.
    public abstract void                putAllValues(final VariableScope scope) throws SetlException;

    public abstract void                putClassDefinition(final String var, final ClassDefinition classDef);

    public abstract Term                scopeToTerm();

    /* -- Debugger -- */

    public abstract void                setBreakpoint(final String id);

    public abstract boolean             removeBreakpoint(final String id);

    public abstract void                removeAllBreakpoints();

    public abstract boolean             isBreakpoint(final String id);

    public abstract String[]            getAllBreakpoints();

    public abstract void                setBreakpointsEnabled(final boolean enabled);

    public boolean                      areBreakpointsEnabled;

    public abstract void                setDebugModeActive(final boolean active);

    public boolean                      isDebugModeActive;

    public abstract void                setDebugPromptActive(final boolean active);

    public abstract boolean             isDebugPromptActive();

    public abstract void                setDebugStepNextExpr(final boolean stepNextExpr);

    public boolean                      isDebugStepNextExpr;

    public abstract void                setDebugStepThroughFunction(final boolean stepThrough);

    public boolean                      isDebugStepThroughFunction;

    public abstract void                setDebugFinishFunction(final boolean finish);

    public boolean                      isDebugFinishFunction;

    public abstract void                setDebugFinishLoop(final boolean finish);

    public boolean                      isDebugFinishLoop;

}


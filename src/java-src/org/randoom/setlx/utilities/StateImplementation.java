package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.expressionUtilities.Iterator;
import org.randoom.setlx.expressions.Assignment;
import org.randoom.setlx.expressions.Call;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.DifferenceAssignment;
import org.randoom.setlx.statements.For;
import org.randoom.setlx.statements.IntegerDivisionAssignment;
import org.randoom.setlx.statements.ModuloAssignment;
import org.randoom.setlx.statements.ProductAssignment;
import org.randoom.setlx.statements.QuotientAssignment;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.statements.SumAssignment;
import org.randoom.setlx.statements.While;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

// This class represents the current state of the interpreter.
public class StateImplementation extends State {
    /* This variable stores the root VariableScope:
       Predefined functions are dynamically loaded into this VariableScope and
       not only into the current one, to be accessible by any previous and future
       VariableScope clones (results in faster lookup).                       */
    private final   static  VariableScope       sROOT_Scope                 = new VariableScope();

    // this scope stores all global variables
    private final           VariableScope       mGlobals;
    private                 boolean             mGlobalsPresent             = false;

    // this variable stores the variable assignment that is currently active
    private                 VariableScope       mVariableScope              = null;

    // interface provider to the outer world
    private                 EnvironmentProvider mEnvProvider                = null;

    private                 LinkedList<String>  mParserErrorCapture         = null;

    // number of CPUs/Cores in System
    private final   static  int                 sCORES                      = Runtime.getRuntime().availableProcessors();

    // is input feed by a human?
    private                 boolean             mIsHuman                    = false;

    // random number generator
    private                 Random              mRandoom                    = null;

    private                 boolean             mStopExecution              = false;
    private                 boolean             mMultiLineMode              = false;
    private                 boolean             mIsInteractive              = false;
    private                 boolean             mPrintVerbose               = false;
    private                 boolean             mTraceAssignments           = false;
    private                 boolean             mAssertsDisabled            = false;

    /* -- Debugger -- */
    private final           HashSet<String>     mBreakpoints                = new HashSet<String>();
    private                 boolean             mBreakpointsEnabled         = false; // are any breakpoints set?

    private                 boolean             mDebugModeActive            = false;
    private                 boolean             mDebugPromptActive          = false;
    private                 boolean             mDebugStepNextExpr          = false;
    private                 boolean             mDebugStepThroughFunction   = false;
    private                 boolean             mDebugFinishFunction        = false;
    private                 boolean             mDebugFinishLoop            = false;

    public StateImplementation() {
        this(DummyEnvProvider.DUMMY);
    }

    public StateImplementation(final EnvironmentProvider envProvider) {
        mGlobals        = new VariableScope();
        mGlobalsPresent = false;
        mVariableScope  = sROOT_Scope.clone();
        mEnvProvider    = envProvider;
    }

    @Override
    public VariableScope getScope() {
        return mVariableScope;
    }

    @Override
    public void setScope(final VariableScope newEnv) {
        mVariableScope = newEnv;
    }

    @Override
    public void resetState() {
        mVariableScope  = sROOT_Scope.clone();
        mGlobals.clear();
        mGlobalsPresent = false;
        ParseSetlX.clearLoadedLibraries();
        mParserErrorCapture.clear();
        mBreakpoints.clear();
    }

    @Override
    public void setEnvironmentProvider(final EnvironmentProvider envProvider) {
        mEnvProvider = envProvider;
    }

    @Override
    public EnvironmentProvider getEnvironmentProvider() {
        return mEnvProvider;
    }

    @Override
    public Value findValue(final String var) {
        Value v = null;
        if (mGlobalsPresent) {
            v = mGlobals.locateValue(var, true);
            if (v != null) {
                return v;
            }
        }
        v = mVariableScope.locateValue(var, ! mGlobalsPresent);
        if (v == null) {
            // search if name matches a predefined function (which start with 'PD_')
            final String packageName = PreDefinedFunction.class.getPackage().getName();
            final String className   = "PD_" + var;
            try {
                final Class<?> c = Class.forName(packageName + '.' + className);
                v                = (PreDefinedFunction) c.getField("DEFINITION").get(null);
            } catch (final Exception e) {
                /* Name does not match predefined function.
                   But return value already is null, no change necessary.     */
            }
            if (v == null && var.toLowerCase(Locale.US).equals(var)) {
               // search if name matches a java Math.x function (which are all lower case)
                try {
                    final Method f = Math.class.getMethod(var, double.class);
                    v        = new MathFunction(var, f);
                } catch (final Exception e) {
                    /* Name also does not match java Math.x function.
                       But return value already is null, no change necessary.     */
                }
            }
            if (v == null) {
                v = Om.OM;
                // identifier could not be looked up...
                // return Om.OM and store it into intial scope to prevent reflection lookup next time
            }
            /* Store result of reflection lookup to root scope to speed up search next time.

               Root scope is chosen, because it is at the end of every
               currently existing and all future scopes search paths.         */
            sROOT_Scope.storeValue(var, v);
        }
        return v;
    }

    @Override
    public void putValue(final String var, final Value value) {
        if (mGlobalsPresent && mGlobals.locateValue(var, false) != null) {
            mGlobals.storeValue(var, value);
        } else {
            mVariableScope.storeValue(var, value);
        }
    }

    @Override
    public boolean putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope) {
        if (mGlobalsPresent) {
            final Value now = mGlobals.locateValue(var, false);
            if (now != null) {
                if (now.equalTo(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return mVariableScope.storeValueCheckUpTo(var, value, outerScope);
    }

    // Add bindings stored in `scope' into current scope.
    // This also adds vars in outer scopes of `scope' until reaching the current
    // scope as outer scope of `scope'.
    @Override
    public void putAllValues(final VariableScope scope) {
        mVariableScope.storeAllValues(mGlobalsPresent, mGlobals, scope);
    }

    @Override
    public void makeGlobal(final String var) {
        if (mGlobals.locateValue(var, false) == null) {
            mGlobals.storeValue(var, Om.OM);
        }
        mGlobalsPresent = true;
    }

    @Override
    public Term scopeToTerm() {
        return mVariableScope.toTerm(this, mGlobals);
    }

    /* I/O */

    @Override
    public String inReadLine() throws JVMIOException {
        return mEnvProvider.inReadLine();
    }

    // write to standard output
    @Override
    public void outWrite(final String msg) {
        mEnvProvider.outWrite(msg);
    }
    @Override
    public void outWriteLn(final String msg) {
        mEnvProvider.outWrite(msg);
        mEnvProvider.outWrite(mEnvProvider.getEndl());
    }
    @Override
    public void outWriteLn() {
        mEnvProvider.outWrite(mEnvProvider.getEndl());
    }

    // write to standard error
    @Override
    public void errWrite(final String msg) {
        mEnvProvider.errWrite(msg);
    }
    @Override
    public void errWriteLn(final String msg) {
        mEnvProvider.errWrite(msg);
        mEnvProvider.errWrite(mEnvProvider.getEndl());
    }
    @Override
    public void errWriteLn() {
        mEnvProvider.errWrite(mEnvProvider.getEndl());
    }

    // capture/write parser errors
    @Override
    public void writeParserErrLn(final String msg) {
        if (mParserErrorCapture != null) {
            mParserErrorCapture.add(msg);
        } else {
            errWriteLn(msg);
        }
    }
    @Override
    public LinkedList<String> getParserErrorCapture() {
        return mParserErrorCapture;
    }
    @Override
    public void setParserErrorCapture(final LinkedList<String> capture) {
        mParserErrorCapture = capture;
    }

    @Override
    public boolean prompt(final String prompt) throws JVMIOException {
        // Only if a pipe is connected the input is ready (has input buffered)
        // BEFORE the prompt.
        // A human usually takes time AFTER the prompt to type something ;-)
        //
        // Also if at one time a prompt was displayed, display all following
        // prompts. (User may continue to type into stdin AFTER we last read
        // from it, causing stdin to be ready, but human controlled)
        if (mIsHuman || ! mEnvProvider.inReady()) {
            mEnvProvider.promptForInput(prompt);
            mIsHuman = true;
            return true;
        }
        return false;
    }

    @Override
    public void promptUnchecked(final String prompt) {
        mEnvProvider.promptForInput(prompt);
    }

    // allow modification of fileName/path when reading files
    @Override
    public String filterFileName(final String fileName) {
        return mEnvProvider.filterFileName(fileName);
    }

    // allow modification of library name
    @Override
    public String filterLibraryName(final String name) {
        return mEnvProvider.filterLibraryName(name);
    }

    /* other stuff */

    @Override
    public int getNumberOfCores() {
        if (sCORES >= 2) {
            return sCORES;
        } else {
            return 1;
        }
    }

    // current time in ms
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public void setPredictableRandoom() {
        mRandoom = new Random(0);
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    @Override
    public int getRandomInt(final int upperBoundary) {
        if (mRandoom == null) {
            mRandoom = new Random();
        }
        return mRandoom.nextInt(upperBoundary);
    }

    // get random number (all int values are possible)
    @Override
    public int getRandomInt() {
        if (mRandoom == null) {
            mRandoom = new Random();
        }
        return mRandoom.nextInt();
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    @Override
    public double getRandomDouble() {
        if (mRandoom == null) {
            mRandoom = new Random();
        }
        return mRandoom.nextDouble();
    }

    @Override
    public Random getRandom() {
        if (mRandoom == null) {
            mRandoom = new Random();
        }
        return mRandoom;
    }

    @Override
    public void stopExecution(final boolean stopExecution) {
        mStopExecution          = stopExecution;
        Block.sStopExecution    = stopExecution;
        Iterator.sStopExecution = stopExecution;
    }

    @Override
    public boolean isExecutionStopped() {
        return mStopExecution;
    }

    @Override
    public void setMultiLineMode(final boolean multiLineMode) {
        mMultiLineMode          = multiLineMode;
    }

    @Override
    public boolean isMultiLineEnabled() {
        return mMultiLineMode;
    }

    @Override
    public void setInteractive(final boolean isInteractive) {
        mIsInteractive = isInteractive;
    }

    @Override
    public boolean isInteractive() {
        return mIsInteractive;
    }

    @Override
    public void setPrintVerbose(final boolean printVerbose) {
        mPrintVerbose   = printVerbose;
    }

    @Override
    public boolean isPrintVerbose() {
        return mPrintVerbose;
    }

    @Override
    public void setTraceAssignments(final boolean traceAssignments) {
        mTraceAssignments                           = traceAssignments;

        Assignment.sTraceAssignments                = traceAssignments;
        DifferenceAssignment.sTraceAssignments      = traceAssignments;
        QuotientAssignment.sTraceAssignments          = traceAssignments;
        IntegerDivisionAssignment.sTraceAssignments = traceAssignments;
        ModuloAssignment.sTraceAssignments          = traceAssignments;
        ProductAssignment.sTraceAssignments         = traceAssignments;
        SumAssignment.sTraceAssignments             = traceAssignments;

        Iterator.sTraceAssignments                  = traceAssignments;
        MatchResult.sTraceAssignments               = traceAssignments;
    }

    @Override
    public boolean isTraceAssignments() {
        return mTraceAssignments;
    }

    @Override
    public void setAssertsDisabled(final boolean assertsDisabled) {
        mAssertsDisabled    = assertsDisabled;
    }

    @Override
    public boolean areAssertsDisabled() {
        return mAssertsDisabled;
    }

    @Override
    public void getLineStart(final StringBuilder sb, final int tabs) {
        if (mPrintVerbose && tabs > 0) {
            final String tab = mEnvProvider.getTab();
            for (int i = 0; i < tabs; ++i) {
                sb.append(tab);
            }
        }
    }

    @Override
    public String getEndl() {
        if (mPrintVerbose) {
            return mEnvProvider.getEndl();
        } else {
            return " ";
        }
    }
    @Override
    public String getTab() {
        if (mPrintVerbose) {
            return mEnvProvider.getTab();
        } else {
            return "";
        }
    }

    /* -- Debugger -- */

    @Override
    public void setBreakpoint(final String id) {
        mBreakpoints.add(id);
        setBreakpointsEnabled(true);
    }

    @Override
    public boolean removeBreakpoint(final String id) {
        final boolean result  = mBreakpoints.remove(id);
        setBreakpointsEnabled(mBreakpoints.size() > 0);
        return result;
    }

    @Override
    public void removeAllBreakpoints() {
        mBreakpoints.clear();
        setBreakpointsEnabled(false);
    }

    @Override
    public boolean isBreakpoint(final String id) {
        return mBreakpoints.contains(id);
    }

    @Override
    public String[] getAllBreakpoints() {
        return mBreakpoints.toArray(new String[0]);
    }

    @Override
    public void setBreakpointsEnabled(final boolean enabled) {
        mBreakpointsEnabled         = enabled;
        Call.sBreakpointsEnabled    = enabled;
    }

    @Override
    public boolean areBreakpointsEnabled() {
        return mBreakpointsEnabled;
    }

    @Override
    public void setDebugModeActive(final boolean active) {
        mDebugModeActive            = active;
        Statement.sDebugModeActive  = active;
    }

    @Override
    public boolean isDebugModeActive() {
        return mDebugModeActive;
    }

    @Override
    public void setDebugPromptActive(final boolean active) {
        mDebugPromptActive  = active;
    }

    @Override
    public boolean isDebugPromptActive() {
        return mDebugPromptActive;
    }

    @Override
    public void setDebugStepNextExpr(final boolean stepNextExpr) {
        mDebugStepNextExpr  = stepNextExpr;
        Expr.sStepNext      = stepNextExpr;
    }

    @Override
    public boolean isDebugStepNextExpr() {
        return mDebugStepNextExpr;
    }

    @Override
    public void setDebugStepThroughFunction(final boolean stepThrough) {
        mDebugStepThroughFunction                   = stepThrough;
        ProcedureDefinition.sStepThroughFunction    = stepThrough;
        PreDefinedFunction.sStepThroughFunction     = stepThrough;
    }

    @Override
    public boolean isDebugStepThroughFunction() {
        return mDebugStepThroughFunction;
    }

    @Override
    public void setDebugFinishFunction(final boolean finish) {
        mDebugFinishFunction                = finish;
        ProcedureDefinition.sFinishFunction = finish;
        Call.sFinishOuterFunction           = finish;
    }

    @Override
    public boolean isDebugFinishFunction() {
        return mDebugFinishFunction;
    }

    @Override
    public void setDebugFinishLoop(final boolean finish) {
        mDebugFinishLoop    = finish;
        For.sFinishLoop     = finish;
        While.sFinishLoop   = finish;
    }

    @Override
    public boolean isDebugFinishLoop() {
        return mDebugFinishLoop;
    }
}


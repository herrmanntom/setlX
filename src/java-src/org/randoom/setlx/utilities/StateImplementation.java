package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.ClassDefinition;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

// This class represents the current state of the interpreter.
public class StateImplementation extends State {

    // interface provider to the outer world
    private                 EnvironmentProvider mEnvProvider;

    private final           HashSet<String>     mLoadedLibraries;
    private                 LinkedList<String>  mParserErrorCapture;
    private                 int                 mParserErrorCount;

    /* This variable stores the root VariableScope:
       Predefined functions are dynamically loaded into this VariableScope and
       not only into the current one, to be accessible by any previous and future
       VariableScope clones (results in faster lookup).                       */
    private final   static  VariableScope       sROOT_Scope = new VariableScope();

    // this scope stores all global variables
    private final           HashMap<String, ClassDefinition> classDefinitions;

    // this variable stores the variable assignment that is currently active
    private                 VariableScope       mVariableScope;

    // number of CPUs/Cores in System
    private final   static  int                 sCORES = Runtime.getRuntime().availableProcessors();

    // is input feed by a human?
    private                 boolean             mIsHuman;

    // random number generator
    private                 Random              mRandoom;

    private                 boolean             mMultiLineMode;
    private                 boolean             mIsInteractive;
    private                 boolean             mPrintVerbose;
    private                 boolean             mAssertsDisabled;

    /* -- Debugger -- */
    private final           HashSet<String>     mBreakpoints;

    private                 boolean             mDebugPromptActive;

    public StateImplementation() {
        this(DummyEnvProvider.DUMMY);
    }

    public StateImplementation(final EnvironmentProvider envProvider) {
        mEnvProvider                     = envProvider;
        mLoadedLibraries                 = new HashSet<String>();
        mParserErrorCapture              = null;
        mParserErrorCount                = 0;
        classDefinitions                 = new HashMap<String, ClassDefinition>();
        mVariableScope                   = sROOT_Scope.createLinkedScope();
        mIsHuman                         = false;
        mRandoom                         = new Random();
        super.isExecutionStopped         = false;
        mMultiLineMode                   = false;
        mIsInteractive                   = false;
        mPrintVerbose                    = false;
        super.traceAssignments           = false;
        mAssertsDisabled                 = false;
        /* -- Debugger -- */
        mBreakpoints                     = new HashSet<String>();
        super.areBreakpointsEnabled      = false;
        super.isDebugModeActive          = false;
        mDebugPromptActive               = false;
        super.isDebugStepNextExpr        = false;
        super.isDebugStepThroughFunction = false;
        super.isDebugFinishFunction      = false;
        super.isDebugFinishLoop          = false;
    }

    @Override
    public void setEnvironmentProvider(final EnvironmentProvider envProvider) {
        mEnvProvider = envProvider;
    }

    @Override
    public EnvironmentProvider getEnvironmentProvider() {
        return mEnvProvider;
    }

    /* -- I/O -- */

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
    public int getParserErrorCount() {
        return mParserErrorCount;
    }

    @Override
    public void addToParserErrorCount(final int numberOfErrors) {
        mParserErrorCount += numberOfErrors;
    }

    @Override
    public void resetParserErrorCount() {
        mParserErrorCount = 0;
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

    @Override
    public boolean isLibraryLoaded(final String name) {
        return mLoadedLibraries.contains(name);
    }

    @Override
    public void libraryWasLoaded(final String name) {
        mLoadedLibraries.add(name);
    }

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
        return mRandoom.nextInt(upperBoundary);
    }

    // get random number (all int values are possible)
    @Override
    public int getRandomInt() {
        return mRandoom.nextInt();
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    @Override
    public double getRandomDouble() {
        return mRandoom.nextDouble();
    }

    @Override
    public Random getRandom() {
        return mRandoom;
    }

    @Override
    public void stopExecution(final boolean stopExecution) {
        super.isExecutionStopped = stopExecution;
    }

    @Override
    public void setMultiLineMode(final boolean multiLineMode) {
        mMultiLineMode = multiLineMode;
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
        mPrintVerbose = printVerbose;
    }

    @Override
    public boolean isPrintVerbose() {
        return mPrintVerbose;
    }

    @Override
    public void setTraceAssignments(final boolean traceAssignments) {
        super.traceAssignments = traceAssignments;
    }

    @Override
    public void setAssertsDisabled(final boolean assertsDisabled) {
        mAssertsDisabled = assertsDisabled;
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

    /* -- saved variables in current scope -- */

    @Override
    public VariableScope getScope() {
        return mVariableScope;
    }

    @Override
    public void setScope(final VariableScope newScope) {
        mVariableScope = newScope;
    }

    @Override
    public void resetState() {
        mVariableScope  = sROOT_Scope.createLinkedScope();
        classDefinitions.clear();
        mLoadedLibraries.clear();
        if (mParserErrorCapture != null) {
            mParserErrorCapture.clear();
        }
        mBreakpoints.clear();
    }

    @Override
    public Value findValue(final String var) throws SetlException {
        Value v = classDefinitions.get(var);
        if (v != null) {
            return v;
        }
        v = mVariableScope.locateValue(this, var, true);
        if (v == null && ! var.equals("this")) {
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
                // return Om.OM and store it into initial scope to prevent reflection lookup next time
            }
            /* Store result of reflection lookup to root scope to speed up search next time.

               Root scope is chosen, because it is at the end of every
               currently existing and all future scopes search paths.         */
            sROOT_Scope.storeValue(var, v);
        }
        return v;
    }

    @Override
    public void putValue(final String var, final Value value) throws IllegalRedefinitionException {
        if (classDefinitions.containsKey(var)) {
            throw new IllegalRedefinitionException(
                "Redefinition of classes is not allowed."
            );
        } else {
            mVariableScope.storeValue(var, value);
        }
    }

    /*
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     * Return false if linked scope contained a different value under this variable,
     * true otherwise.
     */
    @Override
    public boolean putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope) throws SetlException {
        final Value now = classDefinitions.get(var);
        if (now != null) {
            if (now.equalTo(value)) {
                return true;
            } else {
                return false;
            }
        }
        return mVariableScope.storeValueCheckUpTo(this, var, value, outerScope);
    }

    // Add bindings stored in `scope' into current scope.
    // This also adds vars in outer scopes of `scope' until reaching the current
    // scope as outer scope of `scope'.
    @Override
    public void putAllValues(final VariableScope scope) throws SetlException {
        for (final String key : classDefinitions.keySet()) {
            if (scope.locateValue(this, key, false) != null) {
                throw new IllegalRedefinitionException(
                    "Redefinition of classes is not allowed."
                );
            }
        }
        mVariableScope.storeAllValues(scope);
    }

    @Override
    public void putClassDefinition(final String var, final ClassDefinition classDef) {
        classDefinitions.put(var, classDef);
    }

    @Override
    public Term scopeToTerm() {
        return mVariableScope.toTerm(this, classDefinitions);
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
        super.areBreakpointsEnabled = enabled;
    }

    @Override
    public void setDebugModeActive(final boolean active) {
        super.isDebugModeActive = active;
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
        super.isDebugStepNextExpr = stepNextExpr;
    }

    @Override
    public void setDebugStepThroughFunction(final boolean stepThrough) {
        super.isDebugStepThroughFunction = stepThrough;
    }

    @Override
    public void setDebugFinishFunction(final boolean finish) {
        super.isDebugFinishFunction = finish;
    }

    @Override
    public void setDebugFinishLoop(final boolean finish) {
        super.isDebugFinishLoop = finish;
    }
}


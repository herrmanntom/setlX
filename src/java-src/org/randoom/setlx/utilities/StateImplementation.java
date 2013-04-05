package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Real;
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
    private                 EnvironmentProvider envProvider;

    private                 LinkedList<String>  parserErrorCapture;
    private                 int                 parserErrorCount;

    private final           HashSet<String>     loadedLibraries;

    /* This variable stores the root VariableScope:
       Predefined functions are dynamically loaded into this VariableScope and
       not only into the current one, to be accessible by any previous and future
       VariableScope clones (results in faster lookup).                       */
    private final   static  VariableScope       ROOT_SCOPE = new VariableScope();

    // this scope stores all global variables
    private final           HashMap<String, SetlClass> classDefinitions;

    // this variable stores the variable assignment that is currently active
    private                 VariableScope       variableScope;

    // number of CPUs/Cores in System
    private final   static  int                 CORES = Runtime.getRuntime().availableProcessors();

    // is input feed by a human?
    private                 boolean             isHuman;

    // random number generator
    private                 Random              randoom;

    private                 boolean             multiLineMode;
    private                 boolean             isInteractive;
    private                 boolean             printVerbose;
    private                 boolean             assertsDisabled;
    private                 boolean             isRuntimeDebuggingEnabled;

    /* -- Debugger -- */
    private final           HashSet<String>     breakpoints;

    private                 boolean             debugPromptActive;

    public StateImplementation() {
        this(DummyEnvProvider.DUMMY);
    }

    public StateImplementation(final EnvironmentProvider envProvider) {
        this.envProvider                 = envProvider;
        parserErrorCapture               = null;
        parserErrorCount                 = 0;
        super.realPrintMode              = Real.PRINT_MODE_DEFAULT;
        loadedLibraries                  = new HashSet<String>();
        classDefinitions                 = new HashMap<String, SetlClass>();
        variableScope                    = ROOT_SCOPE.createLinkedScope();
        isHuman                          = false;
        randoom                          = new Random();
        super.callStackDepth             = 10; // add a bit to account for initialization stuff
        super.isExecutionStopped         = false;
        multiLineMode                    = false;
        isInteractive                    = false;
        printVerbose                     = false;
        super.traceAssignments           = false;
        assertsDisabled                  = false;
        isRuntimeDebuggingEnabled        = false;
        /* -- Debugger -- */
        breakpoints                      = new HashSet<String>();
        super.areBreakpointsEnabled      = false;
        super.isDebugModeActive          = false;
        debugPromptActive                = false;
        super.isDebugStepNextExpr        = false;
        super.isDebugStepThroughFunction = false;
        super.isDebugFinishFunction      = false;
        super.isDebugFinishLoop          = false;
    }

    @Override
    public void setEnvironmentProvider(final EnvironmentProvider envProvider) {
        this.envProvider = envProvider;
    }

    @Override
    public EnvironmentProvider getEnvironmentProvider() {
        return envProvider;
    }

    /* -- I/O -- */

    @Override
    public String inReadLine() throws JVMIOException {
        return envProvider.inReadLine();
    }

    // write to standard output
    @Override
    public void outWrite(final String msg) {
        envProvider.outWrite(msg);
    }
    @Override
    public void outWriteLn(final String msg) {
        envProvider.outWrite(msg);
        envProvider.outWrite(envProvider.getEndl());
    }
    @Override
    public void outWriteLn() {
        envProvider.outWrite(envProvider.getEndl());
    }

    // write to standard error
    @Override
    public void errWrite(final String msg) {
        envProvider.errWrite(msg);
    }
    @Override
    public void errWriteLn(final String msg) {
        envProvider.errWrite(msg);
        envProvider.errWrite(envProvider.getEndl());
    }
    @Override
    public void errWriteLn() {
        envProvider.errWrite(envProvider.getEndl());
    }

    // capture/write parser errors
    @Override
    public void writeParserErrLn(final String msg) {
        if (parserErrorCapture != null) {
            parserErrorCapture.add(msg);
        } else {
            errWriteLn(msg);
        }
    }
    @Override
    public LinkedList<String> getParserErrorCapture() {
        return parserErrorCapture;
    }
    @Override
    public void setParserErrorCapture(final LinkedList<String> capture) {
        parserErrorCapture = capture;
    }

    @Override
    public int getParserErrorCount() {
        return parserErrorCount;
    }

    @Override
    public void addToParserErrorCount(final int numberOfErrors) {
        parserErrorCount += numberOfErrors;
    }

    @Override
    public void resetParserErrorCount() {
        parserErrorCount = 0;
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
        if (isHuman || ! envProvider.inReady()) {
            envProvider.promptForInput(prompt);
            isHuman = true;
            return true;
        }
        return false;
    }

    @Override
    public void promptUnchecked(final String prompt) {
        envProvider.promptForInput(prompt);
    }

    @Override
    public void setRealPrintMode_default() {
        super.realPrintMode = Real.PRINT_MODE_DEFAULT;
    }
    @Override
    public void setRealPrintMode_engineering() {
        super.realPrintMode = Real.PRINT_MODE_ENGINEERING;
    }
    @Override
    public void setRealPrintMode_plain() {
        super.realPrintMode = Real.PRINT_MODE_PLAIN;
    }

    // allow modification of fileName/path when reading files
    @Override
    public String filterFileName(final String fileName) {
        return envProvider.filterFileName(fileName);
    }

    // allow modification of library name
    @Override
    public String filterLibraryName(final String name) {
        return envProvider.filterLibraryName(name);
    }

    @Override
    public boolean isLibraryLoaded(final String name) {
        return loadedLibraries.contains(name);
    }

    @Override
    public void libraryWasLoaded(final String name) {
        loadedLibraries.add(name);
    }

    @Override
    public int getNumberOfCores() {
        if (CORES >= 2) {
            return CORES;
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
        randoom = new Random(0);
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    @Override
    public int getRandomInt(final int upperBoundary) {
        return randoom.nextInt(upperBoundary);
    }

    // get random number (all int values are possible)
    @Override
    public int getRandomInt() {
        return randoom.nextInt();
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    @Override
    public double getRandomDouble() {
        return randoom.nextDouble();
    }

    @Override
    public Random getRandom() {
        return randoom;
    }

    @Override
    public void stopExecution(final boolean stopExecution) {
        super.isExecutionStopped = stopExecution;
    }

    @Override
    public void setMultiLineMode(final boolean multiLineMode) {
        this.multiLineMode = multiLineMode;
    }

    @Override
    public boolean isMultiLineEnabled() {
        return multiLineMode;
    }

    @Override
    public void setInteractive(final boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    @Override
    public boolean isInteractive() {
        return isInteractive;
    }

    @Override
    public void setPrintVerbose(final boolean printVerbose) {
        this.printVerbose = printVerbose;
    }

    @Override
    public boolean isPrintVerbose() {
        return printVerbose;
    }

    @Override
    public void setTraceAssignments(final boolean traceAssignments) {
        super.traceAssignments = traceAssignments;
    }

    @Override
    public void setAssertsDisabled(final boolean assertsDisabled) {
        this.assertsDisabled = assertsDisabled;
    }

    @Override
    public boolean areAssertsDisabled() {
        return assertsDisabled;
    }

    // 'secret' option to print stack trace of unhandled java exceptions and use more checks
    @Override
    public void setRuntimeDebugging(final boolean isRuntimeDebuggingEnabled) {
        this.isRuntimeDebuggingEnabled = isRuntimeDebuggingEnabled;
    }

    @Override
    public boolean isRuntimeDebuggingEnabled() {
        return isRuntimeDebuggingEnabled;
    }

    @Override
    public void appendLineStart(final StringBuilder sb, final int tabs) {
        if (printVerbose && tabs > 0) {
            final String tab = envProvider.getTab();
            for (int i = 0; i < tabs; ++i) {
                sb.append(tab);
            }
        }
    }

    @Override
    public String getEndl() {
        if (printVerbose) {
            return envProvider.getEndl();
        } else {
            return " ";
        }
    }
    @Override
    public String getTab() {
        if (printVerbose) {
            return envProvider.getTab();
        } else {
            return "";
        }
    }

    /* -- saved variables in current scope -- */

    @Override
    public VariableScope getScope() {
        return variableScope;
    }

    @Override
    public void setScope(final VariableScope newScope) {
        variableScope = newScope;
    }

    @Override
    public void resetState() {
        variableScope  = ROOT_SCOPE.createLinkedScope();
        classDefinitions.clear();
        loadedLibraries.clear();
        if (parserErrorCapture != null) {
            parserErrorCapture.clear();
        }
        breakpoints.clear();
    }

    @Override
    public Value findValue(final String var) throws SetlException {
        Value v = classDefinitions.get(var);
        if (v != null) {
            return v;
        }
        v = variableScope.locateValue(this, var, true);
        if (v == null && ! var.equals("this")) {
            // search if name matches a predefined function (which start with 'PD_')
            final String packageName = PreDefinedProcedure.class.getPackage().getName();
            final String className   = "PD_" + var;
            try {
                final Class<?> c = Class.forName(packageName + '.' + className);
                v                = (PreDefinedProcedure) c.getField("DEFINITION").get(null);
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
            ROOT_SCOPE.storeValue(var, v);
        } else if (v == null) {
            v = Om.OM;
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
            variableScope.storeValue(var, value);
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
        return variableScope.storeValueCheckUpTo(this, var, value, outerScope);
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
        variableScope.storeAllValues(scope);
    }

    @Override
    public void putClassDefinition(final String var, final SetlClass classDef) {
        classDefinitions.put(var, classDef);
    }

    @Override
    public Term scopeToTerm() {
        return variableScope.toTerm(this, classDefinitions);
    }

    /* -- Debugger -- */

    @Override
    public void setBreakpoint(final String id) {
        breakpoints.add(id);
        setBreakpointsEnabled(true);
    }

    @Override
    public boolean removeBreakpoint(final String id) {
        final boolean result  = breakpoints.remove(id);
        setBreakpointsEnabled(breakpoints.size() > 0);
        return result;
    }

    @Override
    public void removeAllBreakpoints() {
        breakpoints.clear();
        setBreakpointsEnabled(false);
    }

    @Override
    public boolean isBreakpoint(final String id) {
        return breakpoints.contains(id);
    }

    @Override
    public String[] getAllBreakpoints() {
        return breakpoints.toArray(new String[0]);
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
        debugPromptActive  = active;
    }

    @Override
    public boolean isDebugPromptActive() {
        return debugPromptActive;
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


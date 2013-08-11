package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * This class represents the current state of the interpreter.
 */
public class State {
    // public variables, available to allow slightly faster access...

    /**
     * Printing mode for doubles
     */
    public                  int                 doublePrintMode;
    /**
     * Print a trace when assigning variables.
     */
    public                  boolean             traceAssignments;
    /**
     * Current call stack depth assumption.
     */
    public                  int                 callStackDepth;
    /**
     * Execution should be terminated at the next possibility.
     */
    public                  boolean             isExecutionStopped;

    // private variables

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
    // measurement of this JVM's stack size
    private         static  int                 STACK_MEASUREMENT  = -1;
    // maximum accepted stack measurement; is roughly equal to -Xss48m using the 64 bit OpenJDK 7
    private final   static  int                 ABSOLUTE_MAX_STACK = 2 * 1024 * 1024;


    // is input feed by a human?
    private                 boolean             isHuman;

    // random number generator
    private                 Random              randoom;

    private                 int                 firstCallStackDepth;

    private                 boolean             isRandoomPredictable;
    private                 boolean             multiLineMode;
    private                 boolean             isInteractive;
    private                 boolean             printVerbose;
    private                 boolean             assertsDisabled;
    private                 boolean             isRuntimeDebuggingEnabled;

    /**
     * Create new state implementation, using a dummy environment.
     */
    public State() {
        this(DummyEnvProvider.DUMMY);
    }

    /**
     * Create new state implementation, using the specified environment.
     *
     * @param envProvider Environment provider implementation to use.
     */
    public State(final EnvironmentProvider envProvider) {
        this.envProvider                 = envProvider;
        parserErrorCapture               = null;
        parserErrorCount                 = 0;
        loadedLibraries                  = new HashSet<String>();
        classDefinitions                 = new HashMap<String, SetlClass>();
        isHuman                          = false;
        isRandoomPredictable             = false;
        multiLineMode                    = false;
        isInteractive                    = false;
        printVerbose                     = false;
        doublePrintMode                    = SetlDouble.PRINT_MODE_DEFAULT;
        traceAssignments                 = false;
        assertsDisabled                  = false;
        isRuntimeDebuggingEnabled        = false;
        resetState();
    }

    /**
     * Reset state to its initial setup.
     *
     * Clears all scopes, class definitions, libraries and error captures.
     */
    public void resetState() {
        if (parserErrorCapture != null) {
            parserErrorCapture.clear();
        }
        parserErrorCount = 0;
        loadedLibraries.clear();
        classDefinitions.clear();
        if (isRandoomPredictable) {
            randoom = new Random(0);
        } else {
            randoom = new Random();
        }
        variableScope       = ROOT_SCOPE.createLinkedScope();
        callStackDepth      = 15; // add a bit to account for initialization stuff
        firstCallStackDepth = -1;
        isExecutionStopped  = false;
    }

    /**
     * Reset the environment provider implementation to use.
     *
     * @param envProvider Environment provider implementation to use.
     */
    public void setEnvironmentProvider(final EnvironmentProvider envProvider) {
        this.envProvider = envProvider;
    }

    /**
     * Get the environment provider implementation currently in use.
     *
     * @return Environment provider implementation currently in use.
     */
    public EnvironmentProvider getEnvironmentProvider() {
        return envProvider;
    }

    /* -- I/O -- */

    /**
     * Read a single line without termination character(s) from user provided
     * input stream (e.g. stdin).
     *
     * @return                Contents of the line read.
     * @throws JVMIOException Thrown in case of IO errors.
     */
    public String inReadLine() throws JVMIOException {
        return envProvider.inReadLine();
    }

    /**
     * Write to standard output.
     *
     * @param msg Message to write.
     */
    public void outWrite(final String msg) {
        envProvider.outWrite(msg);
    }
    /**
     * Write to standard output, appending an endl-sequence.
     *
     * @param msg Message to write.
     */
    public void outWriteLn(final String msg) {
        envProvider.outWrite(msg);
        envProvider.outWrite(envProvider.getEndl());
    }
    /**
     * Write an endl-sequence to standard output.
     */
    public void outWriteLn() {
        envProvider.outWrite(envProvider.getEndl());
    }

    /**
     * Write to standard error.
     *
     * @param msg Message to write.
     */
    public void errWrite(final String msg) {
        envProvider.errWrite(msg);
    }
    /**
     * Write to standard error, appending an endl-sequence.
     *
     * @param msg Message to write.
     */
    public void errWriteLn(final String msg) {
        envProvider.errWrite(msg);
        envProvider.errWrite(envProvider.getEndl());
    }
    /**
     * Write an endl-sequence to standard error.
     */
    public void errWriteLn() {
        envProvider.errWrite(envProvider.getEndl());
    }

    /**
     * Write parser errors to standard error.
     * May capture them for post-processing.
     *
     * @param msg Message to write.
     */
    public void writeParserErrLn(final String msg) {
        if (parserErrorCapture != null) {
            parserErrorCapture.add(msg);
        } else {
            errWriteLn(msg);
        }
    }
    /**
     * Get captured parser error messages.
     *
     * @return List of captured parser error messages.
     */
    public LinkedList<String> getParserErrorCapture() {
        return parserErrorCapture;
    }
    /**
     * Set list to append captured parser error messages to.
     * When capture != null, parser errors will not be written to the output,
     * but instead appended to the provided list.
     *
     * @param capture List to append captured parser error messages to.
     */
    public void setParserErrorCapture(final LinkedList<String> capture) {
        parserErrorCapture = capture;
    }

    /**
     * Get number of parser errors.
     *
     * @return Number of parser errors.
     */
    public int getParserErrorCount() {
        return parserErrorCount;
    }

    /**
     * Increment the current parser error count by the given amount.
     *
     * @param numberOfErrors Number of errors to add to the count.
     */
    public void addToParserErrorCount(final int numberOfErrors) {
        parserErrorCount += numberOfErrors;
    }

    /**
     * Reset the parser error count to 0;
     */
    public void resetParserErrorCount() {
        parserErrorCount = 0;
    }

    /**
     * Write the standard error message, after an otherwise unhandled exception occurred.
     * Prints some extra debug output when runtime debugging is enabled.
     *
     * @param e Exception that occurred.
     */
    public void errWriteInternalError(final Exception e) {
        errWriteLn(
                "Internal error." + envProvider.getEndl() +
                "Please report this error including steps and/or code to reproduce to" +
                "`setlx@randoom.org'."
        );
        if (isRuntimeDebuggingEnabled()) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(out));
            errWrite(out.toString());
        }
    }

    /**
     * Write the standard error message, after the interpreter ran out of stack.
     * Prints some extra debug output when runtime debugging is enabled.
     *
     * @param soe        StackOverflowError that occurred.
     * @param fromParser Set to true if the StackOverflowError occurred in the parser.
     */
    public void errWriteOutOfStack(final StackOverflowError soe, final boolean fromParser) {
        String message = "The setlX ";
        if (fromParser) {
            message += "parser";
        } else {
            message += "interpreter";
        }
        message += " has ran out of stack." + envProvider.getEndl();

        if (fromParser) {
            message += "Please report this error including steps and/or code to reproduce to" +
                       "`setlx@randoom.org'." + envProvider.getEndl();
        } else {
            message += "Try improving the SetlX program to use less recursion." + envProvider.getEndl() +
                       envProvider.getEndl() +
                       "If that does not help get a better device ;-)" + envProvider.getEndl();
        }

        errWriteLn(message);

        if (isRuntimeDebuggingEnabled()) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            soe.printStackTrace(new PrintStream(out));
            errWrite(out.toString());
            errWriteLn("callStackDepth assumption was: " + firstCallStackDepth);
            errWriteLn("max callStackDepth is:         " + getMaxStackSize());
        }
    }

    /**
     * Write the standard error message, after the interpreter ran out of memory.
     *
     * @param showXmxOption Enable hint of JVM options to increase memory size.
     * @param fromParser    Set to true if the out of memory error occurred in the parser.
     */
    public void errWriteOutOfMemory(final boolean showXmxOption, final boolean fromParser) {
        String message = "The setlX ";
        if (fromParser) {
            message += "parser";
        } else {
            message += "interpreter";
        }
        message += " has ran out of memory." + envProvider.getEndl();

        message += "Try improving the SetlX program";
        if (showXmxOption) {
            message += " and/or execute with larger maximum memory size." + envProvider.getEndl() +
                       "(use '-Xmx<size>' parameter for java loader, where <size> is like '6g' [6GB])" + envProvider.getEndl();
        } else {
            message += "." + envProvider.getEndl();
        }
        message += envProvider.getEndl() +
                   "If that does not help get a better device ;-)" + envProvider.getEndl();

        errWriteLn(message);
    }

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

    public void setDoublePrintMode_default() {
        doublePrintMode = SetlDouble.PRINT_MODE_DEFAULT;
    }

    public void setDoublePrintMode_scientific() {
        doublePrintMode = SetlDouble.PRINT_MODE_SCIENTIFIC;
    }

    public void setDoublePrintMode_engineering() {
        doublePrintMode = SetlDouble.PRINT_MODE_ENGINEERING;
    }

    public void setDoublePrintMode_plain() {
        doublePrintMode = SetlDouble.PRINT_MODE_PLAIN;
    }

    // allow modification of fileName/path when reading files

    public String filterFileName(final String fileName) {
        return envProvider.filterFileName(fileName);
    }

    // allow modification of library name

    public String filterLibraryName(final String name) {
        return envProvider.filterLibraryName(name);
    }

    public boolean isLibraryLoaded(final String name) {
        return loadedLibraries.contains(name);
    }

    public void libraryWasLoaded(final String name) {
        loadedLibraries.add(name);
    }

    public int getNumberOfCores() {
        if (CORES >= 2) {
            return CORES;
        } else {
            return 1;
        }
    }

    /**
     * Get current time in milliseconds since 1970-01-01.
     *
     * @return Current time in milliseconds since 1970-01-01.
     */
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public void setRandoomPredictable(final boolean predictableRandoom) {
        isRandoomPredictable = predictableRandoom;
        if (predictableRandoom) {
            randoom = new Random(0);
        }
    }

    public boolean isRandoomPredictable() {
        return isRandoomPredictable;
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public int getRandomInt(final int upperBoundary) {
        return randoom.nextInt(upperBoundary);
    }

    // get random number (all int values are possible)
    public int getRandomInt() {
        return randoom.nextInt();
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public double getRandomDouble() {
        return randoom.nextDouble();
    }

    public Random getRandom() {
        return randoom;
    }

    public int getMaxStackSize() {
        // As setlX's estimation is far from perfect, we assume somewhat more
        // stack usage then its internal accounting guesses.
        // Also a few stack (~66) frames should be free for functions out of our
        // control, like the ones from the JDK ;-)
        //
        // Thus the maximum stack size is about 2/3 of (measured stack - 66).

        return ((measureStackSize() - 66) * 2) / 3;
    }

    // measure the stack size
    private static int measureStackSize() {
        if (STACK_MEASUREMENT <= 0) {
            // create new thread to measure entire stack size, independent of
            // current stack usage size in this thread.
            final Thread stackEstimater = new Thread(new Runnable() {

                @Override
                public void run() {
                    STACK_MEASUREMENT = measureStackSize_slave(2);
                }
            });
            stackEstimater.start();
            try {
                stackEstimater.join();
            } catch (final InterruptedException e) { }
        }
        return STACK_MEASUREMENT;
    }

    private static int measureStackSize_slave(int size) {
        try {
            if (size >= ABSOLUTE_MAX_STACK) {
                // Forever loop protection in case Java ever gets an unlimited stack.
                return ABSOLUTE_MAX_STACK;
            }
            return measureStackSize_slave(++size);
        } catch (final StackOverflowError soe) {
            return size;
        }
    }

    public void storeStackDepthOfFirstCall(final int callStackDepth) {
        if (firstCallStackDepth < 0 && callStackDepth > 0) {
            firstCallStackDepth = callStackDepth;
        }
        if (this.callStackDepth != callStackDepth) {
            // this should not be possible!
            // but reading the parameter prevents javac optimizing the whole thing away
            throw new InvalidParameterException("this.callStackDepth != callStackDepth");
        }
    }

    public void stopExecution(final boolean stopExecution) {
        isExecutionStopped = stopExecution;
    }

    public void setMultiLineMode(final boolean multiLineMode) {
        this.multiLineMode = multiLineMode;
    }

    public boolean isMultiLineEnabled() {
        return multiLineMode;
    }

    public void setInteractive(final boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public void setPrintVerbose(final boolean printVerbose) {
        this.printVerbose = printVerbose;
    }

    public boolean isPrintVerbose() {
        return printVerbose;
    }

    public void setTraceAssignments(final boolean traceAssignments) {
        this.traceAssignments = traceAssignments;
    }

    public void setAssertsDisabled(final boolean assertsDisabled) {
        this.assertsDisabled = assertsDisabled;
    }

    public boolean areAssertsDisabled() {
        return assertsDisabled;
    }

    // 'secret' option to print stack trace of unhandled java exceptions and use more checks
    public void setRuntimeDebugging(final boolean isRuntimeDebuggingEnabled) {
        this.isRuntimeDebuggingEnabled = isRuntimeDebuggingEnabled;
    }

    public boolean isRuntimeDebuggingEnabled() {
        return isRuntimeDebuggingEnabled;
    }

    public void appendLineStart(final StringBuilder sb, final int tabs) {
        if (printVerbose && tabs > 0) {
            final String tab = envProvider.getTab();
            for (int i = 0; i < tabs; ++i) {
                sb.append(tab);
            }
        }
    }

    public String getEndl() {
        if (printVerbose) {
            return envProvider.getEndl();
        } else {
            return " ";
        }
    }

    public String getTab() {
        if (printVerbose) {
            return envProvider.getTab();
        } else {
            return "";
        }
    }

    /* -- saved variables in current scope -- */

    public VariableScope getScope() {
        return variableScope;
    }

    public void setScope(final VariableScope newScope) {
        variableScope = newScope;
    }

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

    /**
     * Store `value' for variable into current scope.
     *
     * @param var                           Variable to set.
     * @param value                         Value to set variable to
     * @param context                       Context description of the assignment for trace.
     * @throws IllegalRedefinitionException Thrown in case of redefining a class.
     */
    public void putValue(final String var, final Value value, final String context) throws IllegalRedefinitionException {
        if (classDefinitions.containsKey(var)) {
            throw new IllegalRedefinitionException(
                "Redefinition of classes is not allowed."
            );
        } else {
            variableScope.storeValue(var, value);
            if (traceAssignments) {
                printTrace(var, value, context);
            }
        }
    }

    /**
     * Print a trace of some assignment into stdout.
     *
     * @param var     Name of the variable that was assigned.
     * @param value   Value that was assigned.
     * @param context Context of the assignment to display, or null.
     */
    public void printTrace(final String var, final Value value, final String context) {
        final StringBuilder out = new StringBuilder();

        out.append("~< Trace");
        if (context != null && ! context.equals("")) {
            out.append(" (");
            out.append(context);
            out.append(")");
        }
        out.append(": ");
        out.append(var);
        out.append(" := ");
        value.appendUnquotedString(this, out, 0);
        out.append(" >~");

        this.outWriteLn(out.toString());
    }

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
    public boolean putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope, final String context) throws SetlException {
        final Value now = classDefinitions.get(var);
        if (now != null) {
            if (now.equalTo(value)) {
                return true;
            } else {
                return false;
            }
        }
        if (traceAssignments) {
            final boolean result = variableScope.storeValueCheckUpTo(this, var, value, outerScope);
            if (result) {
                printTrace(var, value, context);
            }
            return result;
        } else {
            return variableScope.storeValueCheckUpTo(this, var, value, outerScope);
        }
    }

    /**
     * Add bindings stored in `scope' into current scope.
     * This also adds variables in outer scopes of `scope' until reaching the
     * current scope as outer scope of `scope'.
     *
     * @param scope          Scope to set variables from.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public void putAllValues(final VariableScope scope, final String context) throws SetlException {
        for (final String key : classDefinitions.keySet()) {
            if (scope.locateValue(this, key, false) != null) {
                throw new IllegalRedefinitionException(
                    "Redefinition of classes is not allowed."
                );
            }
        }
        if (traceAssignments) {
            final HashMap<String, Value> assignments = new HashMap<String, Value>();
            variableScope.storeAllValuesTrace(scope, assignments);
            for (final Map.Entry<String, Value> entry : assignments.entrySet()) {
                printTrace(entry.getKey(), entry.getValue(), context);
            }
        } else {
            variableScope.storeAllValues(scope);
        }
    }

    public void putClassDefinition(final String var, final SetlClass classDef, final String context) {
        classDefinitions.put(var, classDef);
        if (traceAssignments) {
            printTrace(var, classDef, context);
        }
    }

    public SetlHashMap<Value> getAllVariablesInScope() {
        return variableScope.getAllVariablesInScope(classDefinitions);
    }

    public Term scopeToTerm() {
        return variableScope.toTerm(this, classDefinitions);
    }
}


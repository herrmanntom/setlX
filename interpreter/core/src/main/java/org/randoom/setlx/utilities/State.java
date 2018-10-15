package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlDouble.DoublePrintMode;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.randoom.setlx.utilities.SetlXSourceVersion.SETLX_SOURCE_BUILD;

/**
 * This class represents the current state of the interpreter.
 */
@SuppressWarnings("WeakerAccess")
public class State {

    /**
     * Get version of this setlX binary.
     *
     * @return Implementation version.
     */
    public static String getSetlXVersion() {
        return SetlXSourceVersion.SETLX_SOURCE_VERSION;
    }

    /**
     * Get detailed implementation build number of this setlX binary.
     *
     * @return build identification.
     */
    public static String getSetlXBuildNumber() {
        return SETLX_SOURCE_BUILD.substring(1);
    }

    // public variables, available to allow slightly faster access...

    /**
     * Printing mode for doubles
     */
    public                DoublePrintMode        doublePrintMode;
    /**
     * Print a trace when assigning variables.
     */
    public                boolean                traceAssignments;
    /**
     * Execution should be terminated at the next possibility.
     */
    public                boolean                executionStopped;

    // private variables

    // interface provider to the outer world
    private               EnvironmentProvider    envProvider;

    private               LinkedList<String>     parserErrorCapture;
    private               int                    parserErrorCount;

    private final         HashSet<String>        loadedLibraries;

    /* This variable stores the root VariableScope:
       Predefined functions are dynamically loaded into this VariableScope and
       not only into the current one, to be accessible by any previous and future
       VariableScope clones (results in faster lookup).                       */
    private final         VariableScope          ROOT_SCOPE = VariableScope.createRootScope();

    // this scope stores all global variables
    private final         SetlHashMap<SetlClass> classDefinitions;

    // this variable stores the variable assignment that is currently active
    private               VariableScope          variableScope;

    // number of CPUs/Cores in System
    private final static  int                    CORES = Runtime.getRuntime().availableProcessors();

    // is input feed by a human?
    private               boolean                human;

    // random number generator
    private               Random                 randoom;

    private               boolean                randoomPredictable;
    private               boolean                multiLineMode;
    private               boolean                interactive;
    private               boolean                printVerbose;
    private               boolean                assertsDisabled;
    private               boolean                runtimeDebuggingEnabled;
    private               int                    maxExceptionMessages;

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
        parserErrorCapture      = null;
        parserErrorCount        = 0;
        loadedLibraries         = new HashSet<>();
        classDefinitions        = new SetlHashMap<>();
        human                   = false;
        randoomPredictable      = false;
        multiLineMode           = false;
        interactive             = false;
        printVerbose            = false;
        doublePrintMode         = DoublePrintMode.DEFAULT;
        traceAssignments        = false;
        assertsDisabled         = false;
        runtimeDebuggingEnabled = false;
        maxExceptionMessages    = 40;
        setEnvironmentProvider(envProvider);
        resetState(false);
    }

    /**
     * Reset state to its initial setup.
     *
     * Clears all scopes, class definitions, libraries and error captures.
     */
    public void resetState() {
        resetState(true);
    }

    private void resetState(final boolean cleanup) {
        if (parserErrorCapture != null) {
            parserErrorCapture.clear();
        }
        parserErrorCount = 0;
        loadedLibraries.clear();
        classDefinitions.clear();
        if (randoomPredictable) {
            randoom = new Random(0);
        } else {
            randoom = new Random();
        }
        setScope(ROOT_SCOPE.createLinkedScope());
        if (cleanup) {
            ROOT_SCOPE.clearUndefinedAndInnerBindings();
        }
        executionStopped    = false;
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
    public String inReadLine() throws JVMException {
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
     * @param e \Exception that occurred.\
     */
    public void errWriteInternalError(final Exception e) {
        errWriteLn(
                "Internal error." + envProvider.getEndl() +
                "Please report this error including steps and/or code to reproduce to" +
                "`setlx@randoom.org'."
        );
        errWriteStackTrace(e, false);
    }

    /**
     * Write the stack trace message, after an exception occurred.
     * Only prints output when runtime debugging is enabled.
     *
     * @param t       Exception/Error that occurred.
     * @param isCause Is this throwable the cause of an already printed one?
     */
    public void errWriteStackTrace(final Throwable t, boolean isCause) {
        if (isRuntimeDebuggingEnabled() && t != null) {
            if (isCause) {
                errWrite("Caused by: ");
            }
            errWriteLn(t.getClass().getName() + ": " +  t.getMessage());
            StackTraceElement[] stackTrace = t.getStackTrace();
            int maxExceptionMessages = this.maxExceptionMessages * 2;
            final int end = stackTrace.length;
            final int m_2 = maxExceptionMessages / 2;
            for (int i = 0; i < end; ++i) {
                // leave out some messages in the middle, which are most likely just clutter
                if (end > maxExceptionMessages && i > m_2 - 1 && i < end - (m_2 + 1)) {
                    if (i == m_2) {
                        errWriteLn("   ... \n     omitted " + (end - maxExceptionMessages) + " messages\n   ... ");
                    }
                } else {
                    errWriteLn("  at " + stackTrace[i].toString());
                }
            }
            errWriteStackTrace(t.getCause(), true);
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
            errWriteStackTrace(soe, false);
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
    public boolean prompt(final String prompt) throws JVMException {
        // Only if a pipe is connected the input is ready (has input buffered)
        // BEFORE the prompt.
        // A human usually takes time AFTER the prompt to type something ;-)
        //
        // Also if at one time a prompt was displayed, display all following
        // prompts. (User may continue to type into stdin AFTER we last read
        // from it, causing stdin to be ready, but human controlled)
        if (human || ! envProvider.inReady()) {
            envProvider.promptForInput(prompt);
            human = true;
            return true;
        }
        return false;
    }

    /**
     * Display a question to the user, before forcing to select one of the
     * provided answers.
     *
     * @param question        Question to display.
     * @param answers         Non-empty list of questions to select from.
     * @return                Answer selected by the user.
     * @throws JVMIOException Thrown in case of IO errors.
     */
    public String promptSelectionFromAnswerss(final String question, final List<String> answers) throws JVMException {
        return envProvider.promptSelectionFromAnswers(question, answers);
    }

    /**
     * Set flag for printing doubles with the default way of displaying the exponent.
     */
    public void setDoublePrintMode_default() {
        doublePrintMode = DoublePrintMode.DEFAULT;
    }

    /**
     * Set flag for printing doubles with always displaying the exponent.
     */
    public void setDoublePrintMode_scientific() {
        doublePrintMode = DoublePrintMode.SCIENTIFIC;
    }

    /**
     * Set flag for printing doubles with always displaying a exponent which is a multiple of 3.
     */
    public void setDoublePrintMode_engineering() {
        doublePrintMode = DoublePrintMode.ENGINEERING;
    }

    /**
     * Set flag for printing doubles without displaying the exponent.
     */
    public void setDoublePrintMode_plain() {
        doublePrintMode = DoublePrintMode.PLAIN;
    }

    /**
     * Allow modification of fileName/path when reading files.
     * System dependent changes may be performed by the current environment provider.
     *
     * @param fileName Original fileName/path
     * @return         FileName/path after modification by the environment provider.
     */
    public String filterFileName(final String fileName) {
        return envProvider.filterFileName(fileName);
    }

    /**
     * Allow modification of library name when reading files.
     * System dependent changes may be performed by the current environment provider.
     *
     * @param name Original library name
     * @return     Library name after modification by the environment provider.
     */
    public String filterLibraryName(final String name) {
        return envProvider.filterLibraryName(name);
    }

    /**
     * Check if a library with the given name was already marked as loaded.
     *
     * @param name Name of library loaded.
     * @return     True if library with this name was loaded.
     */
    public boolean isLibraryLoaded(final String name) {
        return loadedLibraries.contains(name);
    }

    /**
     * Mark library with the given name as already loaded.
     *
     * @param name Name of library loaded.
     */
    public void libraryWasLoaded(final String name) {
        loadedLibraries.add(name);
    }

    /**
     * Get number of CPU cores detected in the executing system.
     *
     * @return Number of CPU cores detected.
     */
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

    /**
     * Set internal random number generator into a predictable state.
     *
     * @param predictableRandoom True to set random number generator to be predictable.
     */
    public void setRandoomPredictable(final boolean predictableRandoom) {
        randoomPredictable = predictableRandoom;
        if (predictableRandoom) {
            randoom = new Random(0);
        } else {
            randoom = new Random();
        }
    }

    /**
     * Get if internal random number generator is in a predictable state.
     *
     * @return True if random number generator is predictable.
     */
    @SuppressWarnings("unused")
    public boolean isRandoomPredictable() {
        return randoomPredictable;
    }

    /**
     * Get random number between 0 and upperBoundary (including 0 but not upperBoundary).
     *
     * @param upperBoundary Limit of numbers to return.
     * @return              Random number between 0 and upperBoundary.
     */
    public int getRandomInt(final int upperBoundary) {
        return randoom.nextInt(upperBoundary);
    }

    /**
     * Get random number (all double values are possible).
     *
     * @return Random number.
     */
    public double getRandomDouble() {
        return randoom.nextDouble();
    }

    /**
     * Get internal random number generator object. Use this to hook your randomness
     * into debug options like setRandoomPredictable().
     *
     * @return Random number generator object.
     */
    public Random getRandom() {
        return randoom;
    }

    /**
     * Set flag to stop execution when it is checked by the next statement
     * or expression.
     *
     * @param stopExecution True to stop execution, false otherwise.
     */
    @SuppressWarnings("unused")
    public void stopExecution(final boolean stopExecution) {
        executionStopped = stopExecution;
    }

    /**
     * Only accept input after additional new line when parsing in interactive mode.
     *
     * @param multiLineMode Enable multiple line mode.
     *
     * @see org.randoom.setlx.utilities.ParseSetlX#parseInteractive(State)
     */
    public void setMultiLineMode(final boolean multiLineMode) {
        this.multiLineMode = multiLineMode;
    }

    /**
     * Check if input is only accepted after additional new line when parsing in interactive mode.
     *
     * @return True, if multiple line mode is enabled.
     */
    public boolean isMultiLineEnabled() {
        return multiLineMode;
    }

    /**
     * Flag as interactive session.
     * Mostly influences how certain output is printed.
     *
     * @param interactive True, to enable interactive mode.
     */
    public void setInteractive(final boolean interactive) {
        this.interactive = interactive;
    }

    /**
     * Check if this is an interactive session.
     * Mostly influences how certain output is printed.
     *
     * @return True, to enable interactive mode.
     */
    public boolean isInteractive() {
        return interactive;
    }

    /**
     * Set verbose printing.
     *
     * @param printVerbose True, to enable verbose printing.
     */
    public void setPrintVerbose(final boolean printVerbose) {
        this.printVerbose = printVerbose;
    }

    /**
     * Check if verbose printing is enabled.
     *
     * @return True, to enable verbose printing.
     */
    public boolean isPrintVerbose() {
        return printVerbose;
    }

    /**
     * Set printing a trace when assigning variables.
     *
     * @param traceAssignments True, to enable tracing assignments.
     */
    public void setTraceAssignments(final boolean traceAssignments) {
        this.traceAssignments = traceAssignments;
    }

    /**
     * Set if asserts are enabled and executed.
     *
     * @param assertsDisabled True, to disable asserts.
     */
    public void setAssertsDisabled(final boolean assertsDisabled) {
        this.assertsDisabled = assertsDisabled;
    }

    /**
     * Check if asserts are disabled.
     *
     * @return True, if asserts are disabled.
     */
    public boolean areAssertsDisabled() {
        return assertsDisabled;
    }

    /**
     * 'secret' option to print stack trace of unhandled java exceptions and use
     * more checks.
     *
     * @param isRuntimeDebuggingEnabled True, to enable runtime debugging.
     */
    public void setRuntimeDebugging(final boolean isRuntimeDebuggingEnabled) {
        this.runtimeDebuggingEnabled = isRuntimeDebuggingEnabled;
    }

    /**
     * Get 'secret' option to print stack trace of unhandled java exceptions and
     * use more checks.
     *
     * @return True, if runtime debugging is enabled.
     */
    public boolean isRuntimeDebuggingEnabled() {
        return runtimeDebuggingEnabled;
    }

    /**
     * Get maximum number of messages to print when dumping stack trace.
     *
     * @return Maximum number of messages to print.
     */
    public int getMaxExceptionMessages() {
        return maxExceptionMessages;
    }


    /**
     * Set maximum number of messages to print when dumping stack trace.
     *
     * @param maxExceptionMessages Maximum number of messages to print.
     */
    public void setMaxExceptionMessages(int maxExceptionMessages) {
        this.maxExceptionMessages = maxExceptionMessages;
    }

    /**
     * Append typical beginning of a new line to string builder.
     *
     * @param sb   StringBuilder object to append to.
     * @param tabs Number of tabs to use.
     */
    public void appendLineStart(final StringBuilder sb, final int tabs) {
        if (printVerbose && tabs > 0) {
            final String tab = envProvider.getTab();
            for (int i = 0; i < tabs; ++i) {
                sb.append(tab);
            }
        }
    }

    /**
     * Get the tabulator character to use.
     *
     * @return Tabulator character.
     */
    @SuppressWarnings("unused")
    public String getTab() {
        if (printVerbose) {
            return envProvider.getTab();
        } else {
            return "";
        }
    }

    /**
     * Get system dependent newline character sequence.
     *
     * @return Newline character sequence.
     */
    public String getEndl() {
        if (printVerbose) {
            return envProvider.getEndl();
        } else {
            return " ";
        }
    }

    /**
     * Get identifier for the operating system that setlX runs on.
     *
     * @return Identifier for the operating system executing setlX.
     */
    public String getOsID() {
        return envProvider.getOsID();
    }

    /* -- saved variables in current scope -- */

    /**
     * Get current scope object.
     *
     * @return Current scope.
     */
    public VariableScope getScope() {
        return variableScope;
    }

    /**
     * Set current scope to given scope object.
     *
     * @param newScope scope to set as current scope.
     */
    public void setScope(final VariableScope newScope) {
        variableScope = newScope;
        variableScope.setCurrent();
    }

    /**
     * Get the value of a specific bindings reachable from current scope.
     *
     * @param variable       Name of the variable to locate.
     * @return               Located value or Om.OM.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value findValue(final String variable) throws SetlException {
        Value value = classDefinitions.get(variable);
        if (value != null) {
            return value;
        }
        value = variableScope.locateValue(this, variable);
        if (value == null && ! variable.equals("this")) {
            // search if name matches a predefined function (which start with 'PD_')
            final String packageName = PreDefinedProcedure.class.getPackage().getName();
            final String className   = "PD_" + variable;
            try {
                final Class<?> c = Class.forName(packageName + '.' + className);
                value            = (PreDefinedProcedure) c.getField("DEFINITION").get(null);
            } catch (final Exception e) {
                /* Name does not match predefined function.
                   But return value already is null, no change necessary.     */
            }
            if (value == null && variable.toLowerCase(Locale.US).equals(variable)) {
               // search if name matches a java Math.x function (which are all lower case)
                try {
                    final Method f = Math.class.getMethod(variable, double.class);
                    value          = new MathFunction(variable, f);
                } catch (final Exception e) {
                    /* Name also does not match java Math.x function.
                       But return value already is null, no change necessary.     */
                }
            }
            if (value == null) {
                value = Om.OM;
                // identifier could not be looked up...
                // return Om.OM and store it into initial scope to prevent reflection lookup next time
            }
            /* Store result of reflection lookup to root scope to speed up search next time.

               Root scope is chosen, because it is at the end of every
               currently existing and all future scopes search paths.         */
            ROOT_SCOPE.storeValue(variable, value);
        } else if (value == null || value == VariableScope.ACCESS_DENIED_VALUE) {
            value = Om.OM;
        }
        return value;
    }

    /**
     * Store `value' for variable into current scope.
     *
     * @param var                           Variable to set.
     * @param value                         Value to set variable to.
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
     * @param checkObjects   Also check objects if they have 'value' set in them.
     * @param context        Context description of the assignment for trace.
     * @return               False if linked scope contained a different value under this variable, true otherwise.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public boolean putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope, final boolean checkObjects, final String context) throws SetlException {
        final Value now = classDefinitions.get(var);
        if (now != null) {
            return now.equalTo(value);
        }
        final boolean result = variableScope.storeValueCheckUpTo(this, var, value, outerScope, checkObjects);
        if (traceAssignments && result) {
            printTrace(var, value, context);
        }
        return result;
    }

    /**
     * Store `classDef' into global scope.
     *
     * @param var      Name to bind `classDef' to.
     * @param classDef Class definition to store.
     * @param context  Context description of the assignment for trace.
     */
    public void putClassDefinition(final String var, final SetlClass classDef, final String context) {
        classDefinitions.put(var, classDef);
        if (traceAssignments) {
            printTrace(var, classDef, context);
        }
    }

    /**
     * Collect all bindings reachable from current scope.
     *
     * @return               Map of all reachable bindings.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlHashMap<Value> getAllVariablesInScope() throws SetlException {
        return variableScope.getAllVariablesInScope(this, classDefinitions);
    }

    /**
     * Collect all bindings reachable from current scope and represent them as a term.
     *
     * @return               Term of all reachable bindings.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Term scopeToTerm() throws SetlException {
        return variableScope.toTerm(this, classDefinitions);
    }
}



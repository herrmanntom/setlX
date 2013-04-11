package org.randoom.setlxUI.pc;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.FileNotWriteableException;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.ResetException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.WriteFile;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StateImplementation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing main-function and other glue for the PC version of the setlX interpreter.
 */
public class SetlX {
    private final static String     VERSION         = "2.0.0";
    private final static String     SETLX_URL       = "http://setlX.randoom.org/";
    private final static String     C_YEARS         = "2011-2013";
    private final static String     VERSION_PREFIX  = "v";
    private final static String     HEADER          = "-====================================setlX====================================-";

    private final static int        EXIT_OK         = 0;
    private final static int        EXIT_ERROR      = 1;

    private final static int        EXEC_OK         = 23;
    private final static int        EXEC_ERROR      = 33;
    private final static int        EXEC_EXIT       = 42;

    // print extra information and use correct indentation when printing statements etc
    private       static boolean    verbose         = false;

    public static void main(final String[] args) {
        String              dumpFile     = null;  // file to write loaded code into
        String              dumpJavaFile = null;  // file to write loaded code converted to Java into
        String              dumpTermFile = null;  // file to write loaded code as term into
        boolean             help         = false;
        boolean             interactive  = false;
        boolean             noExecution  = false;
        boolean             termLoop     = false;  // convert loaded code to term and back

        String              expression   = null;  // expression to be evaluated using -ev option
        String              statement    = null;  // code to be executed when using -ex option
        final List<String>  files        = new ArrayList<String>();

        final PcEnvProvider envProvider  = new PcEnvProvider();
        final State         state        = new StateImplementation(envProvider);

        final SetlList parameters = new SetlList(); // can/will be filled later
        try {
            state.putValue("params", parameters);
        } catch (final IllegalRedefinitionException e) {
            // impossible
        }

        if ((envProvider.libraryPath = System.getenv("SETLX_LIBRARY_PATH")) == null) {
            envProvider.libraryPath = "";
        }

        for (int i = 0; i < args.length; ++i) {
            final String s = args[i];
            if (s.equals("--version")) {
                state.outWriteLn(VERSION);

                System.exit(EXIT_OK);

            } else if (s.equals("--dump")) {
                dumpFile = "";
                ++i; // set to next argument
                if (i < args.length) {
                    dumpFile = args[i];
                }
                // check for incorrect dumpFile contents
                if (  dumpFile.equals("") ||
                     (dumpFile.length() >= 2 && dumpFile.substring(0,2).equals("--"))
                   ) {
                    help     = true;
                    dumpFile = null;
                }
            } else if (s.equals("--dumpJava")) {
                dumpJavaFile = "";
                ++i; // set to next argument
                if (i < args.length) {
                    dumpJavaFile = args[i];
                }
                // check for incorrect dumpJavaFile contents
                if (  dumpJavaFile.equals("") ||
                     (dumpJavaFile.length() >= 2 && dumpJavaFile.substring(0,2).equals("--"))
                   ) {
                    help         = true;
                    dumpJavaFile = null;
                }
            } else if (s.equals("--dumpTerm")) {
                dumpTermFile = "";
                ++i; // set to next argument
                if (i < args.length) {
                    dumpTermFile = args[i];
                }
                // check for incorrect dumpTermFile contents
                if (  dumpTermFile.equals("") ||
                     (dumpTermFile.length() >= 2 && dumpTermFile.substring(0,2).equals("--"))
                   ) {
                    help         = true;
                    dumpTermFile = null;
                }
            } else if (s.equals("--ev")) {
                ++i; // set to next argument
                if (i < args.length) {
                    expression = args[i];
                }
                // check for incorrect expression content
                if (  statement != null || expression.equals("") ||
                     (expression.length() >= 2 && expression.substring(0,2).equals("--"))
                   ) {
                    help       = true;
                    expression = null;
                }
            } else if (s.equals("--ex")) {
                ++i; // set to next argument
                if (i < args.length) {
                    statement = args[i];
                }
                // check for incorrect statement content
                if (  expression != null || statement.equals("") ||
                     (statement.length() >= 2 && statement.substring(0,2).equals("--"))
                   ) {
                    help      = true;
                    statement = null;
                }
            } else if (s.equals("--help")) {
                help = true;
            } else if (s.equals("--libraryPath")) {
                ++i; // set to next argument
                if (i < args.length) {
                    envProvider.libraryPath = args[i];
                }
                // check for incorrect contents
                if (  envProvider.libraryPath.equals("") ||
                      (
                          envProvider.libraryPath.length() >= 2 &&
                          envProvider.libraryPath.substring(0,2).equals("--")
                      )
                   ) {
                    help = true;
                }
            } else if (s.equals("--multiLineMode")) {
                state.setMultiLineMode(true);
            } else if (s.equals("--noAssert")) {
                state.setAssertsDisabled(true);
            } else if (s.equals("--noExecution")) {
                noExecution = true;
            } else if (s.equals("--params")) {
                // all remaining arguments are passed into the program
                ++i; // set to next argument
                for (; i < args.length; ++i) {
                    parameters.addMember(state, new SetlString(args[i]));
                }
            } else if (s.equals("--predictableRandom")) { // easier debugging
                state.setPredictableRandoom();
            } else if (s.equals("--real32")) {
                Real.setPrecision32();
            } else if (s.equals("--real64")) {
                Real.setPrecision64();
            } else if (s.equals("--real128")) {
                Real.setPrecision128();
            } else if (s.equals("--real256")) {
                Real.setPrecision256();
            } else if (s.equals("--realDefault")) {
                state.setRealPrintMode_default();
            } else if (s.equals("--realEngineering")) {
                state.setRealPrintMode_engineering();
            } else if (s.equals("--realPlain")) {
                state.setRealPrintMode_plain();
            } else if (s.equals("--runtimeDebugging")) {
                state.setRuntimeDebugging(true);
            } else if (s.equals("--termLoop")) {
                termLoop = true;
            } else if (s.equals("--verbose")) {
                verbose = true;
            } else if (s.length() >= 2 && s.substring(0,2).equals("--")) { // invalid option
                help    = true;
            } else {
                files.add(s);
            }
        }

        // interactive == no files and no code supplied as parameters
        interactive = (files.size() == 0 && expression == null && statement == null);
        // display help if options specify to execute both files and a single expression/statement
        help        = help || (! interactive && files.size() > 0 && (expression != null || statement != null));

        if (interactive || verbose || help) {
            printHeader(state);
            if (! help) {
                printShortHelp(state);
            }
        }
        if (interactive && ! help) {
            printInteractiveBegin(state);
            parseAndExecuteInteractive(state);
        } else if ( ! help ) {
            final List<Block> programs = parseAndEchoCode(
                    state,
                    expression,
                    statement,
                    files,
                    dumpFile,
                    dumpJavaFile,
                    dumpTermFile,
                    termLoop
            );
            if ( ! noExecution) {
                executeFiles(state, programs);
            }
        } else {
            printHelp(state);
        }

        System.exit(EXIT_OK);

    }

    private static void parseAndExecuteInteractive(final State state) {
        state.setInteractive(true);
        Block   blk      = null;
        boolean skipTest = false;
        do {
            try {
                // prompt including newline to visually separate the next input
                state.prompt("\n=> ");
                state.resetParserErrorCount();
                blk         = ParseSetlX.parseInteractive(state);
                if ( ! state.isMultiLineEnabled()) {
                    state.outWriteLn();
                }
                blk.markLastExprStatement();
                skipTest    = false;
            } catch (final EndOfFileException eofe) {
                // user wants to quit
                state.outWriteLn("\n\nGood Bye! (EOF)");

                break;

            } catch (final ParserException pe) {
                state.errWriteLn(pe.getMessage());
                skipTest = true;
                blk      = null;
            } catch (final Exception e) { // this should never happen...
                printInternalError(state);
                if (state.isRuntimeDebuggingEnabled()) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(out));
                    state.errWrite(out.toString());
                }

                break;

            }
        } while (skipTest || (blk != null && execute(state, blk) != EXEC_EXIT));
        printExecutionFinished(state);
    }

    private static List<Block> parseAndEchoCode(
            final State        state,
            final String       expression,
            final String       statement,
            final List<String> files,
            final String       dumpFile,
            final String       dumpJavaFile,
            final String       dumpTermFile,
            final boolean      termLoop
    ) {
        // parsed programs
        int nPrograms = files.size();
        if (expression != null) {
            nPrograms += 1;
        } else if (statement != null) {
            nPrograms += 1;
        }
        final List<Block> programs = new ArrayList<Block>(nPrograms);

        // parse content of all files
        try {
            if (expression != null) {
                final Block exp = new Block();
                exp.add(new ExpressionStatement(ParseSetlX.parseStringToExpr(state, expression)));
                exp.markLastExprStatement();
                programs.add(exp);
            }

            if (statement != null) {
                final Block stmt = ParseSetlX.parseStringToBlock(state, statement);
                stmt.markLastExprStatement();
                programs.add(stmt);
            }

            for (final String fileName : files) {
                programs.add(ParseSetlX.parseFile(state, fileName));
            }
        } catch (final ParserException pe) {
            if (verbose) {
                state.outWriteLn(
                    "-================================Parser=Errors================================-\n"
                );
            }
            state.errWriteLn(pe.getMessage());
            if (verbose) {
                state.outWriteLn(
                    "\n-================================Parsing=Failed===============================-\n"
                );
                state.errWriteLn("Execution terminated due to errors in the input.");
            }

            System.exit(EXIT_ERROR);

        } catch (final OutOfMemoryError oome) {
            printOoError(state);
            System.exit(EXIT_ERROR);

        } catch (final Exception e) { // this should never happen...
            printInternalError(state);
            if (state.isRuntimeDebuggingEnabled()) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(out));
                state.errWrite(out.toString());
            }

            System.exit(EXIT_ERROR);
        }

        // no parser errors when we get here
        if (verbose) {
            state.outWriteLn(
                "-================================Parsed=Program===============================-\n"
            );
        }

        if (termLoop) {
            for (int i = 0; i < programs.size(); ++i) {
                try {
                    programs.set(i, (Block) TermConverter.valueToStatement(programs.get(i).toTerm(state)));
                } catch (final TermConversionException tce) {
                    state.errWriteLn("Error during termLoop!");
                    if (state.isRuntimeDebuggingEnabled()) {
                        final ByteArrayOutputStream out = new ByteArrayOutputStream();
                        tce.printStackTrace(new PrintStream(out));
                        state.errWrite(out.toString());
                    }
                }
            }
        }

        // print and/or dump programs if needed
        if (verbose || dumpFile != null || dumpJavaFile != null || dumpTermFile != null) {
            state.setPrintVerbose(true); // enables correct indentation etc
            for (int i = 0; i < programs.size(); ++i) {
                // get program text
                final String program     = programs.get(i).toString(state) + state.getEndl();
                      String javaProgram = null;
                      String programTerm = null;

                if (dumpJavaFile != null) {
                    // figure out Java class name
                    if (Character.isLowerCase(dumpJavaFile.charAt(0)) ||
                        ! dumpJavaFile.endsWith(".java")) {
                        state.errWriteLn(
                            "File to write Java code into must start with an " +
                            "upper case letter and end in '.java'."
                        );
                        System.exit(EXIT_ERROR);
                    }

                    final String className = dumpJavaFile.substring(0, dumpJavaFile.lastIndexOf(".java"));

                    javaProgram = programs.get(i).toJavaCode(state, className, PcEnvProvider.class);
                }

                if (dumpTermFile != null) {
                    final StringBuilder sb = new StringBuilder();
                    programs.get(i).toTerm(state).canonical(state, sb);
                    programTerm = sb.toString();
                }

                // in verbose mode the parsed programs are echoed
                if (verbose) {
                    state.outWriteLn(program);
                }

                // append program to the dumpFile
                if (dumpFile != null) {
                    try {
                        WriteFile.writeToFile(state, program, dumpFile, /* append = */ (i > 0) );
                    } catch (final FileNotWriteableException fnwe) {
                        state.errWriteLn(fnwe.getMessage());

                        System.exit(EXIT_ERROR);

                    }
                }

                // append programs Java code equivalent to the dumpJavaFile
                if (dumpJavaFile != null) {
                    try {
                        WriteFile.writeToFile(state, javaProgram, dumpJavaFile, /* append = */ (i > 0) );
                    } catch (final FileNotWriteableException fnwe) {
                        state.errWriteLn(fnwe.getMessage());

                        System.exit(EXIT_ERROR);

                    }
                }

                // append programs term equivalent to the dumpJavaFile
                if (dumpTermFile != null) {
                    try {
                        WriteFile.writeToFile(state, programTerm, dumpTermFile, /* append = */ (i > 0) );
                    } catch (final FileNotWriteableException fnwe) {
                        state.errWriteLn(fnwe.getMessage());

                        System.exit(EXIT_ERROR);

                    }
                }
            }
            state.setPrintVerbose(false);
        }

        return programs;
    }

    private static void executeFiles(final State state, final List<Block> programs) {
        state.setInteractive(false);

        if (verbose) {
            printExecutionStart(state);
        }

        // run the parsed code
        for (int program = 0; program < programs.size(); ++program) {
            if (execute(state, programs.get(program)) != EXEC_OK) {
                break; // stop in case of error
            }
            // remove reference to stored code to free some memory
            programs.set(program, null);
        }

        if (verbose) {
            printExecutionFinished(state);
        }
    }

    private static int execute(final State state, final Block b) {
        try {

            state.setDebugModeActive(false);
            b.exec(state);

        } catch (final AbortException ae) { // code detected user did something wrong
            state.errWriteLn(ae.getMessage());
            return EXEC_ERROR;
        } catch (final ExitException ee) { // user/code wants to quit
            if (state.isInteractive()) {
                state.outWriteLn(ee.getMessage());
            }

            return EXEC_EXIT; // breaks loop while parsing interactively

        } catch (final ResetException re) { // user/code wants to quit debugging
            if (state.isInteractive()) {
                state.outWriteLn("Resetting to interactive prompt.");
            }
            return EXEC_OK;
        } catch (final SetlException se) { // user/code did something wrong
            se.printExceptionsTrace(state, 40);
            return EXEC_ERROR;
        } catch (final OutOfMemoryError oome) {
            printOoError(state);
            return EXEC_EXIT; // breaks loop while parsing interactively
        } catch (final Exception e) { // this should never happen...
            printInternalError(state);
            if (state.isRuntimeDebuggingEnabled()) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(out));
                state.errWrite(out.toString());
            }
            return EXEC_ERROR;
        }
        return EXEC_OK; // continue loop while parsing interactively
    }

    private static void printHeader(final State state) {
        // embed version number into header
        final int     versionSize = VERSION.length() + VERSION_PREFIX.length();
        String  header      = HEADER.substring(0, HEADER.length() - (versionSize + 2) );
        header             += VERSION_PREFIX + VERSION + HEADER.substring(HEADER.length() - 2);
        // print header
        state.outWriteLn("\n" + header + "\n");
    }

    private static void printShortHelp(final State state) {
        state.outWriteLn(
            "Welcome to the setlX interpreter!\n" +
            "\n" +
            "Open Source Software from " + SETLX_URL +"\n" +
            "(c) " + C_YEARS + " by Herrmann, Tom\n" +
            "\n" +
            "You can display some helpful information by using '--help' as parameter when\n" +
            "launching this program.\n"
        );
    }

    private static void printInteractiveBegin(final State state) {
        printHelpInteractive(state);
        state.outWriteLn(
            "-===============================Interactive=Mode==============================-"
        );
    }

    private static void printHelpInteractive(final State state) {
        state.outWriteLn(
            "Interactive-Mode:\n" +
            "  The 'exit;' statement terminates the interpreter.\n"
        );
    }

    private static void printHelp(final State state) {
        state.outWriteLn(
            "File paths supplied as parameters for this program will be parsed and executed.\n" +
            "The interactive mode will be started if called without any file parameters.\n"
        );
        printHelpInteractive(state);
        state.outWriteLn(
            "Additional parameters:\n" +
            "  --ev <expression>\n" +
            "     evaluates next argument as expression and exits\n" +
            "  --ex <statement>\n" +
            "     executes next argument as statement and exits\n" +
            "  --libraryPath <path>\n" +
            "     override SETLX_LIBRARY_PATH environment variable\n" +
            "  --multiLineMode\n" +
            "     only accept input in interactive mode after additional new line\n" +
            "  --noAssert\n" +
            "      disables all assert functions\n" +
            "  --noExecution\n" +
            "      load and check code for syntax errors, but do not execute it\n" +
            "  --params <argument> ...\n" +
            "     passes all following arguments to executed program via `params' variable\n" +
            "  --predictableRandom\n" +
            "      always use same random sequence (debugging)\n" +
            "  --real32\n" +
            "  --real64\n" +
            "  --real128\n" +
            "  --real256\n" +
            "      sets the width of the real-type in bits (real64 is the default)\n" +
            "  --realDefault\n" +
            "  --realEngineering\n" +
            "  --realPlain\n" +
            "      sets how the exponent of reals is displayed\n" +
            "  --verbose\n" +
            "      display the parsed program before executing it\n" +
            "  --version\n" +
            "      displays the interpreter version and terminates\n"
        );
    }

    private static void printOoError(final State state) {
        state.errWriteLn(
            "The setlX interpreter has ran out of memory.\n" +
            "Try improving the SetlX program and/or execute with larger maximum memory size.\n" +
            "(use '-Xmx<size>' parameter for java loader, where <size> is like '6g' [6GB])\n" +
            "\n" +
            "If that does not help get a better machine ;-)\n"
        );
    }

    private static void printInternalError(final State state) {
        state.errWriteLn(
            "Internal error. Please report this error including steps and/or code " +
            "to reproduce to `setlx@randoom.org'."
        );
    }

    private static void printExecutionStart(final State state) {
        state.outWriteLn(
            "-===============================Execution=Result==============================-\n"
        );
    }

    private static void printExecutionFinished(final State state) {
        state.outWriteLn(
            "\n-==============================Execution=Finished=============================-\n"
        );
    }
}


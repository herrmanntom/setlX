package org.randoom.setlx.pc.ui;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.WriteFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing main-function and other glue for the PC version of the setlX interpreter.
 */
public class SetlX {
    private final static String  SETLX_URL              = "http://setlX.randoom.org/";
    private final static String  C_YEARS                = "2011-2019";
    private final static String  VERSION_PREFIX         = "v";
    private final static String  HEADER                 = "-====================================setlX====================================-";

    private final static int     EXIT_OK                = 0;
    private final static int     EXIT_ERROR             = 1;

    private final static int     MAX_EXCEPTION_MESSAGES = 40;

    // print extra information and use correct indentation when printing statements etc
    private       static boolean verbose                = false;

    /**
     * The main method.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        String              dumpFile     = null;  // file to write loaded code into
        String              dumpTermFile = null;  // file to write loaded code as term into
        boolean             help         = false;
        boolean             harshWelcome = false; // do not print entire welcome message
        boolean             interactive;
        boolean             noExecution  = false;
        boolean             termLoop     = false; // convert loaded code to term and back

        String              expression   = null;  // expression to be evaluated using -ev option
        String              statement    = null;  // code to be executed when using -ex option
        final List<String>  files        = new ArrayList<>();

        String              libraryPath;
        if ((libraryPath = System.getenv("SETLX_LIBRARY_PATH")) == null) {
            libraryPath = "";
        }

        final PcEnvProvider envProvider  = new PcEnvProvider(libraryPath);
        final State         state        = new State(envProvider);

        final SetlList      parameters   = new SetlList(); // can/will be filled later
        try {
            state.putValue("params", parameters, "init");
        } catch (final IllegalRedefinitionException e) {
            // impossible
        }

        state.setMaxExceptionMessages(MAX_EXCEPTION_MESSAGES);

        // split combined short options like -amn into -a -m -n
        final List<String> arguments = new ArrayList<>(args.length);
        for (final String arg : args) {
            if (arg.matches("-\\{2,}")) {
                for (final char c : arg.substring(1).toCharArray()) {
                    arguments.add("-" + c);
                }
            } else {
                arguments.add(arg);
            }
        }

        for (int i = 0; i < arguments.size(); ++i) {
            final String s = arguments.get(i);
            if (s.equals("--version")) {
                state.outWriteLn(State.getSetlXVersion());
                state.outWriteLn("(Source version: " + State.getSetlXBuildNumber() + ")");

                System.exit(EXIT_OK);

            } else if (s.equals("--doubleDefault")) {
                state.setDoublePrintMode_default();
            } else if (s.equals("--doubleScientific")) {
                state.setDoublePrintMode_scientific();
            } else if (s.equals("--doubleEngineering")) {
                state.setDoublePrintMode_engineering();
            } else if (s.equals("--doublePlain")) {
                state.setDoublePrintMode_plain();
            } else if (s.equals("--dump")) {
                dumpFile = "";
                ++i; // set to next argument
                if (i < arguments.size()) {
                    dumpFile = arguments.get(i);
                }
                // check for incorrect dumpFile contents
                if (  dumpFile.equals("") ||
                     (dumpFile.length() >= 1 && dumpFile.substring(0,1).equals("-"))
                   ) {
                    help     = true;
                    dumpFile = null;
                }
            } else if (s.equals("--dumpTerm")) {
                dumpTermFile = "";
                ++i; // set to next argument
                if (i < arguments.size()) {
                    dumpTermFile = arguments.get(i);
                }
                // check for incorrect dumpTermFile contents
                if (  dumpTermFile.equals("") ||
                     (dumpTermFile.length() >= 1 && dumpTermFile.substring(0,1).equals("-"))
                   ) {
                    help         = true;
                    dumpTermFile = null;
                }
            } else if (s.equals("-e") || s.equals("--eval")) {
                ++i; // set to next argument
                if (i < arguments.size()) {
                    expression = arguments.get(i);
                }
                // check for incorrect expression content
                if (  statement != null || expression.equals("") ||
                     (expression.length() >= 1 && expression.substring(0,1).equals("-"))
                   ) {
                    help       = true;
                    expression = null;
                }
            } else if (s.equals("-x") || s.equals("--exec")) {
                ++i; // set to next argument
                if (i < arguments.size()) {
                    statement = arguments.get(i);
                }
                // check for incorrect statement content
                if (  expression != null || statement.equals("") ||
                     (statement.length() >= 1 && statement.substring(0,1).equals("-"))
                   ) {
                    help      = true;
                    statement = null;
                }
            } else if (s.equals("-h") || s.equals("--harshWelcome")) {
                harshWelcome = true;
            } else if (s.equals("--help")) {
                help = true;
            } else if (s.equals("-l") || s.equals("--libraryPath")) {
                ++i; // set to next argument
                if (i < arguments.size()) {
                    libraryPath = arguments.get(i);
                    envProvider.setlibraryPath(libraryPath);
                }
                // check for incorrect contents
                if (  libraryPath.equals("") ||
                      (
                          libraryPath.length() >= 1 &&
                          libraryPath.substring(0,1).equals("-")
                      )
                   ) {
                    help = true;
                }
            } else if (s.equals("-m") || s.equals("--multiLineMode")) {
                state.setMultiLineMode(true);
            } else if (s.equals("-a") || s.equals("--noAssert")) {
                state.setAssertsDisabled(true);
            } else if (s.equals("-n") || s.equals("--noExecution")) {
                noExecution = true;
            } else if (s.equals("-p") || s.equals("--params")) {
                // all remaining arguments are passed into the program
                ++i; // set to next argument
                for (; i < arguments.size(); ++i) {
                    parameters.addMember(state, new SetlString(arguments.get(i)));
                }
            } else if (s.equals("-r") || s.equals("--predictableRandom")) { // easier debugging
                state.setRandoomPredictable(true);
            } else if (s.equals("--runtimeDebugging")) {
                state.setRuntimeDebugging(true);
            } else if (s.equals("--termLoop")) {
                termLoop = true;
            } else if (s.equals("-v") || s.equals("--verbose")) {
                verbose = true;
            } else if (s.length() >= 1 && s.substring(0,1).equals("-")) { // invalid option
                help    = true;
            } else {
                files.add(s);
            }
        }

        Thread.currentThread().setName("setlXmain");

        // interactive == no files and no code supplied as parameters
        interactive = (files.isEmpty() && expression == null && statement == null);
        // display help if options specify to execute both files and a single expression/statement
        help        = help || (! interactive && files.size() > 0 && (expression != null || statement != null));

        if (interactive || verbose || help) {
            printHeader(state);
            if (! help && ! harshWelcome) {
                printShortHelp(state);
            }
        }
        if (interactive && ! help) {
            if (! harshWelcome) {
                printInteractiveBegin(state);
            }
            parseAndExecuteInteractive(state);
        } else if ( ! help ) {
            final List<Block> programs = parseAndEchoCode(
                    state,
                    expression,
                    statement,
                    files,
                    dumpFile,
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
        Block   blk;
        boolean skipTest;
        do {
            try {
                // prompt including newline to visually separate the next input
                state.prompt("\n=> ");
                state.resetParserErrorCount();
                blk = ParseSetlX.parseInteractive(state);
                if ( ! state.isMultiLineEnabled()) {
                    state.outWriteLn();
                }
                blk.markLastExprStatement();
                skipTest = false;
            } catch (final EndOfFileException eofe) {
                // user wants to quit
                state.outWriteLn("\n\nGood Bye! (EOF)");

                break;

            } catch (final ParserException pe) {
                state.errWriteLn(pe.getMessage());
                skipTest = true;
                blk      = null;

            }  catch (final StackOverflowError soe) {
                state.errWriteOutOfStack(soe, true);

                break;

            } catch (final OutOfMemoryError oome) {
                state.errWriteOutOfMemory(true, true);

                break;

            } catch (final Exception e) { // this should never happen...
                state.errWriteInternalError(e);

                break;

            }
        } while (skipTest || (blk.executeWithErrorHandling(state, true) != Block.EXECUTE.EXIT));
        printExecutionFinished(state);
    }

    private static List<Block> parseAndEchoCode(
            final State        state,
            final String       expression,
            final String       statement,
            final List<String> files,
            final String       dumpFile,
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
        List<Block> programs = new ArrayList<>(nPrograms);

        // parse content of all files
        try {
            if (expression != null) {
                final Block exp = new Block(new ExpressionStatement(ParseSetlX.parseStringToExpr(state, expression)));
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

        } catch (final StackOverflowError soe) {
            state.errWriteOutOfStack(soe, true);
            System.exit(EXIT_ERROR);

        } catch (final OutOfMemoryError oome) {
            state.errWriteOutOfMemory(true, true);
            System.exit(EXIT_ERROR);

        } catch (final Exception e) { // this should never happen...
            state.errWriteInternalError(e);

            System.exit(EXIT_ERROR);
        }

        // no parser errors when we get here
        if (verbose) {
            state.outWriteLn(
                "-================================Parsed=Program===============================-\n"
            );
        }

        if (termLoop) {
            ArrayList<Block> termConvertedPrograms = new ArrayList<>(nPrograms);
            for (Block program : programs) {
                try {
                    Term term = program.toTerm(state);
                    Block recreatedProgram = (Block) Statement.createFromTerm(state, term);
                    if (program.equals(recreatedProgram)) {
                        termConvertedPrograms.add(recreatedProgram);
                    } else {
                        state.errWriteLn("Error during termLoop!");
                        state.errWriteLn("Programs are not equal.");
                    }
                } catch (final SetlException se) {
                    state.errWriteLn("Error during termLoop!");
                    se.printExceptionsTraceAndReplay(state);
                }
            }
            programs = termConvertedPrograms;
        }

        // print and/or dump programs if needed
        if (verbose || dumpFile != null || dumpTermFile != null) {
            state.setPrintVerbose(true); // enables correct indentation etc
            for (int i = 0; i < programs.size(); ++i) {
                // get program text
                final String program     = programs.get(i).toString(state) + state.getEndl();
                      String programTerm = null;

                if (dumpTermFile != null) {
                    final StringBuilder sb = new StringBuilder();
                    try {
                        programs.get(i).toTerm(state).canonical(state, sb);
                    } catch (SetlException se) {
                        state.errWriteLn("Error during termDump!");
                        se.printExceptionsTraceAndReplay(state);
                    }
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
                    } catch (final FileNotWritableException fnwe) {
                        state.errWriteLn(fnwe.getMessage());

                        System.exit(EXIT_ERROR);

                    }
                }

                // append programs term equivalent to the dumpTermFile
                if (dumpTermFile != null) {
                    try {
                        WriteFile.writeToFile(state, programTerm, dumpTermFile, /* append = */ (i > 0) );
                    } catch (final FileNotWritableException fnwe) {
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
            if (programs.get(program).executeWithErrorHandling(state, true) != Block.EXECUTE.OK) {
                break; // stop in case of error
            }
            // remove reference to stored code to free some memory
            programs.set(program, null);
        }

        if (verbose) {
            printExecutionFinished(state);
        }
    }

    private static void printHeader(final State state) {
        // embed version number into header
        String version = State.getSetlXVersion();
        final int     versionSize = version.length() + VERSION_PREFIX.length();
        String  header      = HEADER.substring(0, HEADER.length() - (versionSize + 2) );
        header             += VERSION_PREFIX + version + HEADER.substring(HEADER.length() - 2);
        // print header
        state.outWriteLn("\n" + header);
    }

    private static void printShortHelp(final State state) {
        state.outWriteLn(
            "\n" +
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
            "\n" +
            "File paths supplied as parameters for this program will be parsed and executed.\n" +
            "The interactive mode will be started if called without any file parameters.\n"
        );
        printHelpInteractive(state);
        state.outWriteLn(
            "Additional parameters:\n" +
            "  -l <path>, --libraryPath <path>\n" +
            "      Override SETLX_LIBRARY_PATH environment variable.\n" +
            "  -a, --noAssert\n" +
            "      Disables all assert functions.\n" +
            "  -n, --noExecution\n" +
            "      Load and check code for syntax errors, but do not execute it.\n" +
            "  -r, --predictableRandom\n" +
            "      Always use the same sequence of random numbers (useful for debugging).\n" +
            "  -p <argument> ..., --params <argument> ...\n" +
            "      Pass all following arguments to executed program via `params' variable.\n" +
            "  -e <expression>, --eval <expression>\n" +
            "      Evaluates next argument as expression and exits.\n" +
            "  -x <statement>, --exec <statement>\n" +
            "      Executes next argument as statement and exits.\n" +
            "  -v, --verbose\n" +
            "      Display the parsed program before executing it.\n" +
            "  --doubleDefault\n" +
            "  --doubleScientific\n" +
            "  --doubleEngineering\n" +
            "  --doublePlain\n" +
            "      Sets how the exponent of a floating point number is printed.\n" +
            "  -h, --harshWelcome\n" +
            "      Interactive mode: Reduce welcome message to a bare minimum.\n" +
            "  -m, --multiLineMode\n" +
            "      Interactive mode: Input is only processed after an additional new line.\n" +
            "  --version\n" +
            "      Display interpreter version and terminate.\n"
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


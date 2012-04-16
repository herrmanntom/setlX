import interpreter.exceptions.AbortException;
import interpreter.exceptions.BreakException;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.EndOfFileException;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.FileNotReadableException;
import interpreter.exceptions.FileNotWriteableException;
import interpreter.exceptions.ParserException;
import interpreter.exceptions.ResetException;
import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.Real;
import interpreter.utilities.DumpSetlX;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SetlX {

    private final static String VERSION         = "0.7.0";
    private final static String VERSION_PREFIX  = "v";
    private final static String HEADER          = "-====================================setlX====================================-";

    private final static int    EXIT_OK         = 0;
    private final static int    EXIT_ERROR      = 1;

    private final static int    EXEC_OK         = 23;
    private final static int    EXEC_ERROR      = 33;
    private final static int    EXEC_EXIT       = 42;

    public static void main(String[] args) throws Exception {
        boolean         dump        = false; // writes loaded code into a file, including internal line numbers
        String          dumpFile    = "";    // file to dump into
        boolean         help        = false;
        boolean         interactive = false;
        boolean         noExecution = false;
        boolean         verbose     = false; /* print extra information and use correct indentation when
                                                   printing statements etc.                                 */
        List<String>    files       = new LinkedList<String>();

        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.equals("--version")) {
                System.out.println(VERSION);

                System.exit(EXIT_OK);

            } else if (s.equals("--dump")) {
                dump = true;
                i++; // set to next argument
                if (i < args.length) {
                    dumpFile = args[i];
                }
                if ( (dumpFile.equals("")                                           ) || // no next argument
                     (dumpFile.length() >= 2 && dumpFile.substring(0,2).equals("--"))    // some option is next argument
                   ) {
                    help = true;
                    dump = false;
                }
            } else if (s.equals("--help")) {
                help = true;
            } else if (s.equals("--noExecution")) {
                noExecution = true;
            } else if (s.equals("--predictableRandom")) { // easier debugging
                Environment.setPredictableRandoom();
            } else if (s.equals("--real32")) {
                Real.setPrecision32();
            } else if (s.equals("--real64")) {
                Real.setPrecision64();
            } else if (s.equals("--real128")) {
                Real.setPrecision128();
            } else if (s.equals("--real256")) {
                Real.setPrecision256();
            } else if (s.equals("--verbose")) {
                verbose = true;
            } else if (s.length() >= 2 && s.substring(0,2).equals("--")) { // invalid option
                help    = true;
            } else {
                files.add(s);
            }
        }
        interactive = (files.size() == 0); // interactive == no files supplied as parameters

        if (interactive || verbose || help) {
            printHeader();
            if (! help) {
                printShortHelp();
            }
        }
        if (interactive && ! help) {
            printInteractiveBegin();
            parseAndExecuteInteractive();
        } else if ( ! help) {
            List<Block> programs = parseAndDumpFiles(files, dump, dumpFile, verbose);
            if ( ! noExecution) {
                executeFiles(programs, verbose);
            }
        } else {
            printHelp();
        }

        System.exit(EXIT_OK);

    }

    private static void parseAndExecuteInteractive() throws Exception {
        Environment.setInteractive(true);
        Environment.setPrintAfterEval(true);
        Block   blk      = null;
        boolean skipTest = false;
        do {
            System.out.println(); // newline to visually separate the next input
            System.out.print("=> ");
            System.out.flush();
            try {
                ParseSetlX.resetErrorCount();
                blk         = ParseSetlX.parseInteractive();
                skipTest    = false;
            } catch (EndOfFileException eofe) {
                // user wants to quit
                System.out.println("\n\nGood Bye! (EOF)");

                break;

            } catch (ParserException pe) {
                System.err.println("\nLast input not executed due to errors in it.");
                skipTest = true;
                blk      = null;
            } catch (NullPointerException e) { // this should never happen...
                System.err.println("Internal Error. Please report this error including the code you typed.");

                break;

            }
        } while (skipTest || (blk != null && execute(blk) != EXEC_EXIT));
        printExecutionFinished();
    }

    private static List<Block> parseAndDumpFiles(List<String> files, boolean dump, String dumpFile, boolean verbose) throws Exception {
        // parsed programs
        List<Block> programs = new ArrayList<Block>(files.size());

        if (verbose) {
            System.out.println("-================================Parser=Errors================================-\n");
        }

        // parse content of all files (Antlr will print its parser errors into stderr ...)
        try {
            for (String fileName : files) {
                programs.add(ParseSetlX.parseFile(fileName));
            }
        } catch (ParserException pe) {
            if (pe instanceof FileNotReadableException) {
                System.err.println(pe.getMessage());
            }
            if (verbose) {
                System.out.println("\n-================================Parsing=Failed===============================-\n");
            }
            System.err.println("Execution terminated due to errors in the input.");

            System.exit(EXIT_ERROR);

        } catch (NullPointerException e) { // this should never happen...
            System.err.println("Internal Error. Please report this error including the code you loaded.");

            System.exit(EXIT_ERROR);
        }

        // no parser errors when we get here
        if (verbose) {
            System.out.println("none\n");
            System.out.println("-================================Parsed=Program===============================-\n");
        }

        // print and/or dump programs if needed
        if (verbose || dump) {
            Environment.setPrintVerbose(true); // enables correct indentation etc
            for (int i = 0; i < programs.size(); i++) {
                // get program text
                String program = programs.get(i).toString() + '\n';

                //in verbose mode the parsed programs are echoed
                if (verbose) {
                    System.out.print(program);
                }

                // when dump is enabled, the program is appended to the dumpFile
                if (dump) {
                    try {
                        DumpSetlX.dumpToFile(program, dumpFile, /* append = */ (i > 0) );
                    } catch (FileNotWriteableException fnwe) {
                        System.err.println(fnwe.getMessage());

                        System.exit(EXIT_ERROR);

                    }
                }
            }
            Environment.setPrintVerbose(false);
        }

        return programs;
    }

    private static void executeFiles(List<Block> programs, boolean verbose) throws Exception {
        Environment.setInteractive(false);
        Environment.setPrintAfterEval(false);

        if (verbose) {
            printExecutionStart();
        }

        // run the parsed code
        for (Block blk : programs) {
            if (execute(blk) != EXEC_OK) {
                break; // stop in case of error
            }
        }

        if (verbose) {
            printExecutionFinished();
        }
    }

    private static int execute(Block b) {
        try {

            Environment.setDebugModeActive(false);
            b.execute();

        } catch (AbortException ae) { // code detected user did something wrong
            System.err.println(ae.getMessage());
            return EXEC_ERROR;
        } catch (BreakException be) { // break outside of procedure
            if (Environment.isInteractive()) {
                System.out.println(be.getMessage());
            }
            return EXEC_ERROR;
        } catch (ContinueException ce) { // continue outside of procedure
            if (Environment.isInteractive()) {
                System.out.println(ce.getMessage());
            }
            return EXEC_ERROR;
        } catch (ExitException ee) { // user/code wants to quit
            if (Environment.isInteractive()) {
                System.out.println(ee.getMessage());
            }

            return EXEC_EXIT; // breaks loop while parsing interactively

        } catch (ResetException re) { // user/code wants to quit debugging
            if (Environment.isInteractive()) {
                System.out.println("Resetting to interactive prompt.");
            }
            return EXEC_OK;
        } catch (ReturnException re) { // return outside of procedure
            if (Environment.isInteractive()) {
                System.out.println(re.getMessage());
            }
            return EXEC_ERROR;
        } catch (SetlException se) { // user/code did something wrong
            printExceptionsTrace(se.getTrace());
            return EXEC_ERROR;
        } catch (NullPointerException e) { // this should never happen...
            System.err.println("Internal Error. Please report this error including the code you executed.");
            return EXEC_ERROR;
        } catch (OutOfMemoryError oome) {
            System.err.println("Out of memory error.\n"
                             + "Try improving the SetlX program and/or execute with larger maximum memory size.\n"
                             + "(use '-Xmx<size>' parameter for java loader, where <size> is like '6g' [6GB])\n"
                             + "\n"
                             + "If that does not help get a better machine ;-)\n");
            return EXEC_EXIT; // breaks loop while parsing interactively
        }
        return EXEC_OK; // continue loop while parsing interactively
    }

    private static void printHeader() {
        // embed version number into header
        int     versionSize = VERSION.length() + VERSION_PREFIX.length();
        String  header      = HEADER.substring(0, HEADER.length() - (versionSize + 2) );
        header             += VERSION_PREFIX + VERSION + HEADER.substring(HEADER.length() - 2);
        // print header
        System.out.println("\n" + header + "\n");
    }

    private static void printShortHelp() {
        System.out.println("Welcome to the setlX interpreter!\n"
                         + "\n"
                         + "You can display some helpful information by using '--help' as parameter when\n"
                         + "launching this program.\n");
    }

    private static void printInteractiveBegin() {
        printHelpInteractive();
        System.out.print("-===============================Interactive=Mode==============================-\n");
    }

    private static void printHelpInteractive() {
        System.out.println("Interactive-Mode:\n"
                         + "  Two newline characters execute previous input.\n"
                         + "  The 'exit;' statement terminates the interpreter.\n");
    }

    private static void printHelp() {
        System.out.println("File paths supplied as parameters for this program will be parsed and executed.\n"
                         + "The interactive mode will be started if called without any file parameters.\n");
        printHelpInteractive();
        System.out.println("Additional parameters:\n"
                         + "  --dump <file-name>\n"
                         + "      writes loaded code into a file\n"
                         + "  --noExecution\n"
                         + "      load and check code for syntax errors, but do not execute it\n"
                         + "  --predictableRandom\n"
                         + "      always use same random sequence (debugging)\n"
                         + "  --real32\n"
                         + "  --real64\n"
                         + "  --real128\n"
                         + "  --real256\n"
                         + "      sets the width of the real-type in bits (real64 is the default)\n"
                         + "  --verbose\n"
                         + "      display the parsed program before executing it\n"
                         + "  --version\n"
                         + "      displays the interpreter version and terminates\n");
    }

    private static void printExecutionStart() {
        System.out.println("\n-===============================Execution=Result==============================-\n");
    }

    private static void printExecutionFinished() {
        System.out.println("\n-==============================Execution=Finished=============================-\n");
    }

    private static void printExceptionsTrace(List<String> trace) {
        int end = trace.size();
        int max = 40;
        int m_2 = max / 2;
        for (int i = 0; i < end; ++i) {
            // leave out some messages in the middle, which are most likely just clutter
            if (end > max && i > m_2 - 1 && i < end - (m_2 + 1)) {
                if (i == m_2) {
                    System.err.println(" ... \n     omitted " + (end - max) + " messages\n ... ");
                }
            } else {
                System.err.println(trace.get(i));
            }
        }
    }
}


import interpreter.exceptions.EndOfFileException;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.FileNotReadableException;
import interpreter.exceptions.ParserException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.SyntaxErrorException;
import interpreter.statements.Block;
import interpreter.types.Real;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.LinkedList;
import java.util.List;

public class SetlX {

    private final static String VERSION         = "0.2.2";
    private final static String VERSION_PREFIX  = "v";
    private final static String HEADER          = "-====================================setlX====================================-";

    public static void main(String[] args) throws Exception {
        boolean            help        = false;
        boolean            interactive = false;
        boolean            verbose     = false; /* print extra information and use correct indentation when
                                                   printing statements etc.                                 */
        LinkedList<String> files       = new LinkedList<String>();

        for (String s: args) {
            if (s.equals("--version")) {
                System.out.println(VERSION);
                return;
            } else if (s.equals("--help")) {
                help = true;
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
        } else if (! help) {
            for (String file: files) {
                parseAndExecuteFile(file, verbose);
            }
        } else {
            printHelp();
        }
    }

    private static void parseAndExecuteInteractive() throws Exception {
        Environment.setInteractive(true);
        Block   blk      = null;
        boolean skipTest = false;
        do {
            System.out.println(); // newline to visually separate the next input
            System.out.print("=> ");
            try {
                blk         = ParseSetlX.parseInteractive();
                skipTest    = false;
            } catch (EndOfFileException eofe) {
                // user wants to quit
                System.out.println("\n\nGood Bye! (EOF)");
                break;
            } catch (ParserException pe) {
                System.err.println("\nLast input not executed due to previous errors.");
                skipTest = true;
                blk      = null;
            }
        } while (skipTest || (blk != null && execute(blk)));
        printExecutionFinished();
    }

    private static void parseAndExecuteFile(String fileName, boolean verbose) throws Exception {
        Environment.setInteractive(false);
        if (verbose) {
            System.out.println("-================================Parser=Errors================================-\n");
        }

        // parse the file contents (Antlr will print its parser errors into stderr ...)
        Block   blk = null;
        try {
            blk = ParseSetlX.parseFile(fileName);
        } catch (ParserException pe) {
            if (pe instanceof FileNotReadableException) {
                System.err.println(pe.getMessage());
            }
            if (verbose) {
                System.out.println("\n-================================Parsing=Failed===============================-\n");
            }
            System.err.println("Execution terminated due to previous errors.");
            return; // terminate execution
        }

        // no parser errors when we get here
        if (verbose) {
            System.out.println("none\n");
        }

        // in verbose mode the parsed program is echoed
        if (verbose) {
            System.out.println("-================================Parsed=Program===============================-\n");
            Environment.setPrintVerbose(true); // enables correct indentation etc
            System.out.println(blk + "\n");
            Environment.setPrintVerbose(false);
            System.out.println("-===============================Execution=Result==============================-\n");
        }

        // run the parsed code
        execute(blk);

        if (verbose) {
            printExecutionFinished();
        }
    }

    private static boolean execute(Block b) {
        try {

            b.execute();

        } catch (ExitException ee) { // user/code wants to quit
            System.out.println(ee.getMessage());

            return false; // breaks loop while parsing interactively

        } catch (SetlException se) { // user/code did something wrong
            printExceptionsTrace(se.getTrace());
        } catch (NullPointerException e) { // code syntax was not parsed correctly
            System.err.println("Syntax Error.");
        } catch (OutOfMemoryError oome) {
            System.err.println("Out of memory error.\n"
                             + "Try improving the SetlX program and/or execute with larger maximum memory size.\n"
                             + "(use '-Xmx<size>' parameter for java loader, where <size> is like '6g' [6GB])\n"
                             + "\n"
                             + "If that does not help get a better machine ;-)\n");
            return false; // breaks loop while parsing interactively
        }
        return true; // continue loop while parsing interactively
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

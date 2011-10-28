import grammar.*;

import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.SetlReal;
import interpreter.utilities.Environment;
import interpreter.utilities.InputReader;

import org.antlr.runtime.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SetlX {

    private final static String VERSION         = "0.1.1";
    private final static String VERSION_PREFIX  = "v";
    private final static String HEADER          = "=====================================setlX======================================";

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
                SetlReal.setPrecision32();
            } else if (s.equals("--real64")) {
                SetlReal.setPrecision64();
            } else if (s.equals("--real128")) {
                SetlReal.setPrecision128();
            } else if (s.equals("--real256")) {
                SetlReal.setPrecision256();
            } else if (s.equals("--verbose")) {
                verbose = true;
            } else if (s.substring(0,2).equals("--")) { // invalid option
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
            parseInteractive();
        } else if (! help) {
            for (String file: files) {
                parseFile(file, verbose);
            }
        } else {
            printHelp();
        }
    }

    private static void parseInteractive() throws Exception {
        Environment.setInteractive(true);
        Block blk = null;
        do {
            System.out.print("=> ");
            try {
                InputStream         stream = InputReader.getStream();
                ANTLRInputStream    input  = new ANTLRInputStream(stream);
                SetlXgrammarLexer   lexer  = new SetlXgrammarLexer(input);
                CommonTokenStream   ts     = new CommonTokenStream(lexer);
                SetlXgrammarParser  parser = new SetlXgrammarParser(ts);
                blk = parser.block();
            } catch (EOFException eof) {
                break;
            }
        } while (blk != null && execute(blk));
        printExecutionFinished();
    }

    private static void parseFile(String fileName, boolean verbose) throws Exception {
        Environment.setInteractive(false);
        try {
            ANTLRStringStream   input  = new ANTLRFileStream(fileName);
            SetlXgrammarLexer   lexer  = new SetlXgrammarLexer(input);
            CommonTokenStream   ts     = new CommonTokenStream(lexer);
            SetlXgrammarParser  parser = new SetlXgrammarParser(ts);

            if (verbose) {
                System.out.println("=================================Parser=Errors==================================\n");
            }
            // parse the file contents
            Block               blk    = parser.block();

            // now Antlr will print its parser errors into the output ...

            if (parser.getNumberOfSyntaxErrors() > 0) {
                if (verbose) {
                    System.out.println("\n=================================Parsing=Failed=================================\n");
                } else {
                    System.err.println("Execution terminated due to previous errors.");
                }

                return; // terminate execution

            } else if (verbose) {
                System.out.println("none\n"); // no parser errors
            }

            // in verbose mode the parsed program is echoed
            if (verbose) {
                System.out.println("=================================Parsed=Program=================================\n");
                Environment.setPrintVerbose(true); // enables correct indentation etc
                System.out.println(blk + "\n");
                Environment.setPrintVerbose(false);
                System.out.println("================================Execution=Result================================\n");
            }

            // run the parsed code
            execute(blk);

            if (verbose) {
                printExecutionFinished();
            }
        } catch (IOException e) {
            System.err.println("File " + fileName + " could not be read.");
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
        }

        if (Environment.isInteractive()) {
            System.out.println(); // newline to visually separate the next input
        }

        return true; // continue loop while parsing interactively
    }

    private static void printHeader() {
        // embed version number into header
        int     versionSize = VERSION.length() + VERSION_PREFIX.length();
        String  header      = HEADER.substring(0, HEADER.length() - (versionSize + 1) );
        header             += VERSION_PREFIX + VERSION + HEADER.substring(HEADER.length() - 1);
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
        System.out.println("================================Interactive=Mode================================\n");
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
        System.out.println("\n===============================Execution=Finished===============================\n");
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

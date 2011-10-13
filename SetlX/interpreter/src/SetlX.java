import grammar.*;
import interpreter.Environment;
import interpreter.InputReader;
import interpreter.InterpreterProgram;
import interpreter.Program;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;
import interpreter.types.SetlReal;

import org.antlr.runtime.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SetlX {

    private final static String VERSION         = "1.0.1";
    private final static String VERSION_PREFIX  = "v";
    private final static String HEADER          = "=====================================SetlX======================================";

    public static void main(String[] args) throws Exception {
        boolean            help        = false;
        boolean            interactive = false;
        boolean            verbose     = false;
        LinkedList<String> files       = new LinkedList<String>();

        for (String s: args) {
            if (s.equals("--version")) {
                System.out.println(VERSION);
                return;
            } else if (s.equals("--help")) {
                help = true;
            } else if (s.equals("--predictableRandom")) { //easier debugging
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
            } else if (s.substring(0,2).equals("--")) {
                help    = true;
            } else {
                files.add(s);
            }
        }
        interactive = files.size() == 0;

        if (interactive || verbose || help) {
            // embed version number into header
            int     versionSize = VERSION.length() + VERSION_PREFIX.length();
            String  header      = HEADER.substring(0, HEADER.length() - (versionSize + 1) );
            header             += VERSION_PREFIX + VERSION + HEADER.substring(HEADER.length() - 1);
            // print header
            System.out.println("\n" + header + "\n");
            if (! help) {
                System.out.println("Welcome to the SetlX Interpreter Tom Herrmann!\n\n"
                                 + "You can display some helpful information by using '--help' as parameter when\n"
                                 + "launching this program.\n");
            }
        }
        if (interactive && !help) {
            printHelpInteractive();
            parseInteractive();
        } else if (! help) {
            for (String file: files) {
                parseFile(file, verbose);
            }
        } else {
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
    }

    private static void parseInteractive() throws Exception {
        Environment.setInteractive(true);
        System.out.println("================================Interactive=Mode================================\n");
        InterpreterProgram p = null;
        do {
            try {
                InputStream         stream = InputReader.getStream();
                ANTLRInputStream    input  = new ANTLRInputStream(stream);
                SetlXLexer          lexer  = new SetlXLexer(input);
                CommonTokenStream   ts     = new CommonTokenStream(lexer);
                SetlXParser         parser = new SetlXParser(ts);
                p = parser.setlInterpreterProgram();
            } catch (EOFException eof) {
                break;
            }
        } while (p != null && executeProgram(p));
        printExecutionFinished();
    }

    private static void parseFile(String fileName, boolean verbose) throws Exception {
        Environment.setInteractive(false);
        try {
            ANTLRStringStream   input  = new ANTLRFileStream(fileName);
            SetlXLexer          lexer  = new SetlXLexer(input);
            CommonTokenStream   ts     = new CommonTokenStream(lexer);
            SetlXParser         parser = new SetlXParser(ts);
            if (verbose) {
                System.out.println("=================================Parser=Errors==================================\n");
            }
            Program             p      = parser.fullSetlProgram();
            if (parser.getNumberOfSyntaxErrors() > 0) {
                if (verbose) {
                    System.out.println("\n=================================Parsing=Failed=================================\n");
                } else {
                    System.err.println("Execution terminated due to previous errors.");
                }
                return;
            } else if (verbose) {
                System.out.println("none\n");
            }
            if (verbose) {
                System.out.println("=================================Parsed=Program=================================\n");
                Environment.setPrintVerbose(true);
                System.out.println(p + "\n");
                Environment.setPrintVerbose(false);
                System.out.println("================================Execution=Result================================\n");
            }
            executeProgram(p);
            if (verbose) {
                printExecutionFinished();
            }
        } catch (IOException e) {
            System.err.println("File " + fileName + " could not be read.");
        }
    }

    private static boolean executeProgram(InterpreterProgram p) {
        try {
            p.execute();
        } catch (ExitException ee) {
            if (Environment.isInteractive()) {
                System.err.print("-- ");
            }
            System.err.println(ee.getMessage());
            return false;
        } catch (SetlException se) {
            if (Environment.isInteractive()) {
                System.err.println("/*");
            }
            List<String> trace = se.getTrace();
            int end = trace.size();
            int max = 40;
            int m_2 = max / 2;
            for (int i = 0; i < end; ++i) {
                if (end > max && i > m_2 - 1 && i < end - (m_2 + 1)) {
                    if (i == m_2) {
                        System.err.println(" ... \n     omitted " + (end - max) + " messages\n ... ");
                    }
                } else {
                    System.err.println(trace.get(i));
                }
            }
            if (Environment.isInteractive()) {
                System.err.println("*/");
            }
        } catch (NullPointerException e) {
            System.err.println("-- Syntax Error.");
        }
        if (Environment.isInteractive()) {
            System.out.println();
        }
        return true;
    }

    private static void printHelpInteractive() {
        System.out.println("Interactive-Mode:\n"
                         + "  Two newline characters execute previous input.\n"
                         + "  The 'exit;' statement (outside of loop statements) terminates the interpreter.\n");
    }

    private static void printExecutionFinished() {
        System.out.println("\n===============================Execution=Finished===============================\n");
    }
}

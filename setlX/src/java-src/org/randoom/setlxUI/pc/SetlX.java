package org.randoom.setlxUI.pc;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.BreakException;
import org.randoom.setlx.exceptions.ContinueException;
import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.FileNotWriteableException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.ResetException;
import org.randoom.setlx.exceptions.ReturnException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.utilities.DumpSetlX;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.EnvironmentProvider;
import org.randoom.setlx.utilities.ParseSetlX;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SetlX {

    private final static String     VERSION         = "0.7.8";
    private final static String     SETLX_URL       = "http://setlX.randoom.org/";
    private final static String     C_YEARS         = "2011-2012";
    private final static String     VERSION_PREFIX  = "v";
    private final static String     HEADER          = "-====================================setlX====================================-";

    private final static int        EXIT_OK         = 0;
    private final static int        EXIT_ERROR      = 1;

    private final static int        EXEC_OK         = 23;
    private final static int        EXEC_ERROR      = 33;
    private final static int        EXEC_EXIT       = 42;

    /* global parameters */
    // 'secret' option to print stack trace of unhandled java exceptions
    private       static boolean    unhideExceptions= false;
    // print extra information and use correct indentation when printing statements etc
    private       static boolean    verbose         = false;

    // This interface class provides access to the I/O mechanisms of the target platform etc
    private static class PcEnvProvider implements EnvironmentProvider {

        private final static String         sTAB            = "\t";
        private       static String         sENDL           = null;

        // buffered reader for stdin
        private       static BufferedReader sStdInReader    = null;

        private static BufferedReader getStdIn() {
            if (sStdInReader == null) {
                sStdInReader = new BufferedReader(new InputStreamReader(System.in));
            }
            return sStdInReader;
        }

        /* interface functions */

        // read from input
        public boolean  inReady() throws JVMIOException {
            try {
                return getStdIn().ready();
            } catch (IOException ioe) {
                throw new JVMIOException("Unable to open stdIn!");
            }
        }
        public String   inReadLine() throws JVMIOException {
            try {
                       // line is read and returned without termination character(s)
                return getStdIn().readLine();
            } catch (IOException ioe) {
                throw new JVMIOException("Unable to open stdIn!");
            }
        }

        // write to standard output
        public void     outWrite(String msg) {
            System.out.print(msg);
        }

        // write to standard error
        public void     errWrite(String msg) {
            System.err.print(msg);
        }

        // prompt for user input
        public void    promptForInput(String msg) {
            System.out.print(msg);
            System.out.flush();
        }

        // some text format stuff
        public String   getTab() {
            return sTAB;
        }
        public String   getEndl() {
            if (sENDL == null) {
                sENDL = System.getProperty("line.separator");
            }
            return sENDL;
        }

        // allow modification of fileName/path when reading files
        public String   filterFileName(String fileName) {
            return fileName; // not required on PC
        }
    }

    public static void main(String[] args) throws Exception {
        boolean         dump        = false; // writes loaded code into a file
        String          dumpFile    = "";    // file to dump into
        boolean         help        = false;
        boolean         interactive = false;
        boolean         noExecution = false;
        List<String>    files       = new ArrayList<String>();

        // initialize Environment
        Environment.setEnvironmentProvider(new PcEnvProvider());

        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.equals("--version")) {
                Environment.outWriteLn(VERSION);

                System.exit(EXIT_OK);

            } else if (s.equals("--dump")) {
                dump = true;
                i++; // set to next argument
                if (i < args.length) {
                    dumpFile = args[i];
                }
                // check for incorrect dumpFile contents
                if (  dumpFile.equals("") ||
                     (dumpFile.length() >= 2 && dumpFile.substring(0,2).equals("--"))
                   ) {
                    help = true;
                    dump = false;
                }
            } else if (s.equals("--help")) {
                help = true;
            } else if (s.equals("--noAssert")) {
                Environment.setAssertsDisabled(true);
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
            } else if (s.equals("--unhideExceptions")) {
                unhideExceptions = true;
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
            List<Block> programs = parseAndDumpFiles(files, dump, dumpFile);
            if ( ! noExecution) {
                executeFiles(programs);
            }
        } else {
            printHelp();
        }

        System.exit(EXIT_OK);

    }

    private static void parseAndExecuteInteractive() throws Exception {
        Environment.setInteractive(true);
        Block   blk      = null;
        boolean skipTest = false;
        do {
            // prompt including newline to visually separate the next input
            Environment.prompt("\n=> ");
            try {
                ParseSetlX.resetErrorCount();
                blk         = ParseSetlX.parseInteractive();
                blk.markLastExprStatement();
                skipTest    = false;
            } catch (EndOfFileException eofe) {
                // user wants to quit
                Environment.outWriteLn("\n\nGood Bye! (EOF)");

                break;

            } catch (ParserException pe) {
                Environment.errWriteLn("\nLast input not executed due to errors in it.");
                skipTest = true;
                blk      = null;
            } catch (Exception e) { // this should never happen...
                printInternalError();
                if (unhideExceptions) {
                    e.printStackTrace();
                }

                break;

            }
        } while (skipTest || (blk != null && execute(blk) != EXEC_EXIT));
        printExecutionFinished();
    }

    private static List<Block> parseAndDumpFiles(List<String> files,
                                                 boolean      dump,
                                                 String       dumpFile) throws Exception {
        // parsed programs
        List<Block> programs = new ArrayList<Block>(files.size());

        if (verbose) {
            Environment.outWriteLn(
                "-================================Parser=Errors================================-\n"
            );
        }

        // parse content of all files (ANTLR will print its parser errors into stderr ...)
        try {
            for (String fileName : files) {
                programs.add(ParseSetlX.parseFile(fileName));
            }
        } catch (ParserException pe) {
            if (pe instanceof FileNotReadableException) {
                Environment.errWriteLn(pe.getMessage());
            }
            if (verbose) {
                Environment.outWriteLn(
                    "\n-================================Parsing=Failed===============================-\n"
                );
            }
            Environment.errWriteLn("Execution terminated due to errors in the input.");

            System.exit(EXIT_ERROR);

        } catch (Exception e) { // this should never happen...
            printInternalError();
            if (unhideExceptions) {
                e.printStackTrace();
            }

            System.exit(EXIT_ERROR);
        }

        // no parser errors when we get here
        if (verbose) {
            Environment.outWriteLn("none\n");
            Environment.outWriteLn(
                "-================================Parsed=Program===============================-\n"
            );
        }

        // print and/or dump programs if needed
        if (verbose || dump) {
            Environment.setPrintVerbose(true); // enables correct indentation etc
            for (int i = 0; i < programs.size(); i++) {
                // get program text
                String program = programs.get(i).toString() + '\n';

                //in verbose mode the parsed programs are echoed
                if (verbose) {
                    Environment.outWriteLn(program);
                }

                // when dump is enabled, the program is appended to the dumpFile
                if (dump) {
                    try {
                        DumpSetlX.dumpToFile(program, dumpFile, /* append = */ (i > 0) );
                    } catch (FileNotWriteableException fnwe) {
                        Environment.errWriteLn(fnwe.getMessage());

                        System.exit(EXIT_ERROR);

                    }
                }
            }
            Environment.setPrintVerbose(false);
        }

        return programs;
    }

    private static void executeFiles(List<Block> programs) throws Exception {
        Environment.setInteractive(false);

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
            Environment.errWriteLn(ae.getMessage());
            return EXEC_ERROR;
        } catch (BreakException be) { // break outside of procedure
            if (Environment.isInteractive()) {
                Environment.outWriteLn(be.getMessage());
            }
            return EXEC_ERROR;
        } catch (ContinueException ce) { // continue outside of procedure
            if (Environment.isInteractive()) {
                Environment.outWriteLn(ce.getMessage());
            }
            return EXEC_ERROR;
        } catch (ExitException ee) { // user/code wants to quit
            if (Environment.isInteractive()) {
                Environment.outWriteLn(ee.getMessage());
            }

            return EXEC_EXIT; // breaks loop while parsing interactively

        } catch (ResetException re) { // user/code wants to quit debugging
            if (Environment.isInteractive()) {
                Environment.outWriteLn("Resetting to interactive prompt.");
            }
            return EXEC_OK;
        } catch (ReturnException re) { // return outside of procedure
            if (Environment.isInteractive()) {
                Environment.outWriteLn(re.getMessage());
            }
            return EXEC_ERROR;
        } catch (SetlException se) { // user/code did something wrong
            printExceptionsTrace(se.getTrace());
            return EXEC_ERROR;
        } catch (OutOfMemoryError oome) {
            Environment.errWriteLn(
                "The setlX interpreter has ran out of memory.\n" +
                "Try improving the SetlX program and/or execute with larger maximum memory size.\n" +
                "(use '-Xmx<size>' parameter for java loader, where <size> is like '6g' [6GB])\n" +
                "\n" +
                "If that does not help get a better machine ;-)\n"
            );
            return EXEC_EXIT; // breaks loop while parsing interactively
        } catch (Exception e) { // this should never happen...
            printInternalError();
            if (unhideExceptions) {
                e.printStackTrace();
            }
            return EXEC_ERROR;
        }
        return EXEC_OK; // continue loop while parsing interactively
    }

    private static void printHeader() {
        // embed version number into header
        int     versionSize = VERSION.length() + VERSION_PREFIX.length();
        String  header      = HEADER.substring(0, HEADER.length() - (versionSize + 2) );
        header             += VERSION_PREFIX + VERSION + HEADER.substring(HEADER.length() - 2);
        // print header
        Environment.outWriteLn("\n" + header + "\n");
    }

    private static void printShortHelp() {
        Environment.outWriteLn(
            "Welcome to the setlX interpreter!\n" +
            "\n" +
            "Open Source Software from " + SETLX_URL +"\n" +
            "(c) " + C_YEARS + " by Herrmann, Tom\n" +
            "\n" +
            "You can display some helpful information by using '--help' as parameter when\n" +
            "launching this program.\n"
        );
    }

    private static void printInteractiveBegin() {
        printHelpInteractive();
        Environment.outWriteLn(
            "-===============================Interactive=Mode==============================-"
        );
    }

    private static void printHelpInteractive() {
        Environment.outWriteLn(
            "Interactive-Mode:\n" +
            "  Two newline characters execute previous input.\n" +
            "  The 'exit;' statement terminates the interpreter.\n"
        );
    }

    private static void printHelp() {
        Environment.outWriteLn(
            "File paths supplied as parameters for this program will be parsed and executed.\n" +
            "The interactive mode will be started if called without any file parameters.\n"
        );
        printHelpInteractive();
        Environment.outWriteLn(
            "Additional parameters:\n" +
            "  --noAssert\n" +
            "      disables all assert functions\n" +
            "  --noExecution\n" +
            "      load and check code for syntax errors, but do not execute it\n" +
            "  --predictableRandom\n" +
            "      always use same random sequence (debugging)\n" +
            "  --real32\n" +
            "  --real64\n" +
            "  --real128\n" +
            "  --real256\n" +
            "      sets the width of the real-type in bits (real64 is the default)\n" +
            "  --verbose\n" +
            "      display the parsed program before executing it\n" +
            "  --version\n" +
            "      displays the interpreter version and terminates\n"
        );
    }

    private static void printInternalError() {
        Environment.errWriteLn(
            "Internal Error. Please report this error including steps and/or code " +
            "to reproduce to `setlx@randoom.org'."
        );
    }

    private static void printExecutionStart() {
        Environment.outWriteLn(
            "\n-===============================Execution=Result==============================-\n"
        );
    }

    private static void printExecutionFinished() {
        Environment.outWriteLn(
            "\n-==============================Execution=Finished=============================-\n"
        );
    }

    private static void printExceptionsTrace(List<String> trace) {
        int end = trace.size();
        int max = 40;
        int m_2 = max / 2;
        for (int i = end - 1; i >= 0; --i) {
            // leave out some messages in the middle, which are most likely just clutter
            if (end > max && i > m_2 - 1 && i < end - (m_2 + 1)) {
                if (i == m_2) {
                    Environment.errWriteLn(" ... \n     omitted " + (end - max) + " messages\n ... ");
                }
            } else {
                Environment.errWriteLn(trace.get(i));
            }
        }
    }
}


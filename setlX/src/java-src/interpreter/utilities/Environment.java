package interpreter.utilities;

import java.util.Random;

// This class provides environment variables

public class Environment {

    // last source line read/computed
    public          static  int     sourceLine          = 0;

    // number of CPUs (cores) in the executing system
    private final   static  int     CORES               = Runtime.getRuntime().availableProcessors();

    // random number generator
    private         static  Random  randoom             = null;

    private         static  boolean sIsInteractive      = false;
    private         static  boolean sPrintAfterEval     = false;
    private         static  boolean sPrintLineNumbers   = false; // for debugging the interpreter
    private         static  boolean sPrintVerbose       = false;

    private final   static  String  TAB                 = "\t";
    private final   static  String  ENDL                = "\n";

    public static int getNumberOfCores() {
        if (CORES >= 2) {
            return CORES;
        } else {
            return 1;
        }
    }

    public static void setPredictableRandoom() {
        randoom = new Random(0);
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public static int getRandomInt(int upperBoundary) {
        if (randoom == null) {
            randoom = new Random();
        }
        return randoom.nextInt(upperBoundary);
    }

    public static void setInteractive(boolean isInteractive) {
        sIsInteractive = isInteractive;
    }

    public static boolean isInteractive() {
        return sIsInteractive;
    }

    public static void setPrintAfterEval(boolean isPrintAfterEval) {
        sPrintAfterEval = isPrintAfterEval;
    }

    public static boolean isPrintAfterEval() {
        return sPrintAfterEval;
    }

    public static void setPrintLineNumbers(boolean isPrintLineNumbers) {
        sPrintLineNumbers = isPrintLineNumbers;
    }

    public static boolean isPrintLineNumbers() {
        return sPrintLineNumbers;
    }

    public static void setPrintVerbose(boolean printVerbose) {
        sPrintVerbose       = printVerbose;
    }

    public static boolean isPrintVerbose() {
        return sPrintVerbose;
    }

    public static String getLineStart(int tabs) {
        return getLineStart(-1, tabs);
    }

    public static String getLineStart(int lineNr, int tabs) {
        if (!sPrintVerbose) {
            return "";
        }
        String r = null;
        if (sPrintLineNumbers) {
            r = lineNrToStr(lineNr) + TAB;
        } else {
            r = "";
        }
        for (int i = 0; i < tabs; i++) {
            r += TAB;
        }
        return r;
    }

    public static String getEndl() {
        if (sPrintVerbose) {
            return ENDL;
        } else {
            return " ";
        }
    }

    private static String lineNrToStr(int lineNr) {
        String r = "";
        if (lineNr > 0) {
            r += lineNr;
        }
        while (r.length() < ("" + sourceLine).length()) {
            r = " " + r;
        }
        return "/* " + r + " */ ";
    }
}


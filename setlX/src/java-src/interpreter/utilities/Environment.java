package interpreter.utilities;

import java.util.Random;

// This class provides environment variables

public class Environment {

    // number of CPUs (cores) in the executing system
    private final   static  int     CORES               = Runtime.getRuntime().availableProcessors();

    // random number generator
    private         static  Random  randoom             = null;

    private         static  boolean sIsInteractive      = false;
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

    public static void setPrintVerbose(boolean printVerbose) {
        sPrintVerbose       = printVerbose;
    }

    public static boolean isPrintVerbose() {
        return sPrintVerbose;
    }

    public static String getTabs(int tabs) {
        if (tabs <= 0 || !sPrintVerbose) {
            return "";
        }
        String r = TAB;
        for (int i = 1; i < tabs; i++) {
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
}


package interpreter.utilities;

import java.util.Random;

// This class provides environment variables

public class Environment {
    /*============================ static ============================*/

    // random number generator
    private static Random               randoom         = null;

    private static boolean              sIsInteractive  = false;
    private static boolean              sPrintVerbose   = false;

    private static String               sTab            = "\t";

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
        sPrintVerbose = printVerbose;
    }

    public static boolean isPrintVerbose() {
        return sPrintVerbose;
    }

    public static String getTabs(int tabs) {
        if (tabs <= 0 || !sPrintVerbose) {
            return "";
        }
        String r = sTab;
        for (int i = 1; i < tabs; i++) {
            r += sTab;
        }
        return r;
    }

    public static String getEndl() {
        if (sPrintVerbose) {
            return "\n";
        } else {
            return " ";
        }
    }

}

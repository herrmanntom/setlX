package org.randoom.setlx.utilities;

import org.randoom.setlx.expressions.Call;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.statements.For;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.statements.While;
import org.randoom.setlx.types.ProcedureDefinition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

// This class provides environment variables

public class Environment {
    // number of CPUs (cores) in the executing system
    private final   static  int             sCORES                      = Runtime.getRuntime().availableProcessors();

    // buffered reader for stdin
    private         static  BufferedReader  sStdInReader                = null;
    private         static  boolean         sIsHuman                    = false;

    // random number generator
    private         static  Random          sRandoom                    = null;

    private         static  boolean         sIsInteractive              = false;
    private         static  boolean         sPrintAfterEval             = false;
    private         static  boolean         sPrintVerbose               = false;
    private         static  boolean         sAssertsDisabled            = false;

    private final   static  String          sTAB                        = "\t";
    private final   static  String          sENDL                       = "\n";

    /* -- debugger -- */
    private final   static  HashSet<String> sBreakpoints                = new HashSet<String>();
    private         static  boolean         sBreakpointsEnabled         = false; // are any breakpoints set?

    private         static  boolean         sDebugModeActive            = false;
    private         static  boolean         sDebugPromptActive          = false;
    private         static  boolean         sDebugStepNextExpr          = false;
    private         static  boolean         sDebugStepThroughFunction   = false;
    private         static  boolean         sDebugFinishFunction        = false;
    private         static  boolean         sDebugFinishLoop            = false;

    public static int getNumberOfCores() {
        if (sCORES >= 2) {
            return sCORES;
        } else {
            return 1;
        }
    }

    public static BufferedReader getStdIn() {
        if (sStdInReader == null) {
            sStdInReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return sStdInReader;
    }

    public static boolean promptForStdInOnStdOut(String prompt) throws IOException {
        // Only if a pipe is connected the input is ready (has input buffered)
        // BEFORE the prompt.
        // A human usually takes time AFTER the prompt to type something ;-)
        //
        // Also if at one time a prompt was displayed, display all following
        // prompts. (User may continue to type into stdin AFTER we last read
        // from it, causing stdin to be ready, but human controlled)
        if (sIsHuman || ! getStdIn().ready()) {
            System.out.print(prompt);
            System.out.flush();
            sIsHuman = true;
            return true;
        }
        return false;
    }

    public static void setPredictableRandoom() {
        sRandoom = new Random(0);
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public static int getRandomInt(int upperBoundary) {
        if (sRandoom == null) {
            sRandoom = new Random();
        }
        return sRandoom.nextInt(upperBoundary);
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

    public static void setPrintVerbose(boolean printVerbose) {
        sPrintVerbose   = printVerbose;
    }

    public static boolean isPrintVerbose() {
        return sPrintVerbose;
    }

    public static void setAssertsDisabled(boolean assertsDisabled) {
        sAssertsDisabled    = assertsDisabled;
    }

    public static boolean areAssertsDisabled() {
        return sAssertsDisabled;
    }

    public static String getLineStart(int tabs) {
        if (!sPrintVerbose || tabs <= 0) {
            return "";
        }
        String r = sTAB;
        for (int i = 1; i < tabs; i++) {
            r += sTAB;
        }
        return r;
    }

    public static String getEndl() {
        if (sPrintVerbose) {
            return sENDL;
        } else {
            return " ";
        }
    }

    public static void setBreakpoint(String id) {
        sBreakpoints.add(id);
        setBreakpointsEnabled(true);
    }

    public static boolean removeBreakpoint(String id) {
        boolean result  = sBreakpoints.remove(id);
        setBreakpointsEnabled(sBreakpoints.size() > 0);
        return result;
    }

    public static void removeAllBreakpoints() {
        sBreakpoints.clear();
        setBreakpointsEnabled(false);
    }

    public static boolean isBreakpoint(String id) {
        return sBreakpoints.contains(id);
    }

    public static String[] getAllBreakpoints() {
        return sBreakpoints.toArray(new String[0]);
    }

    public static void setBreakpointsEnabled(boolean enabled) {
        sBreakpointsEnabled         = enabled;
        Call.sBreakpointsEnabled    = enabled;
    }

    public static boolean areBreakpointsEnabled() {
        return sBreakpointsEnabled;
    }

    public static void setDebugModeActive(boolean active) {
        sDebugModeActive            = active;
        Statement.sDebugModeActive  = active;
    }

    public static boolean isDebugModeActive() {
        return sDebugModeActive;
    }

    public static void setDebugPromptActive(boolean active) {
        sDebugPromptActive  = active;
    }

    public static boolean isDebugPromptActive() {
        return sDebugPromptActive;
    }

    public static void setDebugStepNextExpr(boolean stepNextExpr) {
        sDebugStepNextExpr  = stepNextExpr;
        Expr.sStepNext      = stepNextExpr;
    }

    public static boolean isDebugStepNextExpr() {
        return sDebugStepNextExpr;
    }

    public static void setDebugStepThroughFunction(boolean stepThrough) {
        sDebugStepThroughFunction                   = stepThrough;
        ProcedureDefinition.sStepThroughFunction    = stepThrough;
        PreDefinedFunction.sStepThroughFunction     = stepThrough;
    }

    public static boolean isDebugStepThroughFunction() {
        return sDebugStepThroughFunction;
    }

    public static void setDebugFinishFunction(boolean finish) {
        sDebugFinishFunction                = finish;
        ProcedureDefinition.sFinishFunction = finish;
        Call.sFinishOuterFunction           = finish;
    }

    public static boolean isDebugFinishFunction() {
        return sDebugFinishFunction;
    }

    public static void setDebugFinishLoop(boolean finish) {
        sDebugFinishLoop    = finish;
        For.sFinishLoop     = finish;
        While.sFinishLoop   = finish;
    }

    public static boolean isDebugFinishLoop() {
        return sDebugFinishLoop;
    }
}


package interpreter.utilities;

import interpreter.expressions.Call;
import interpreter.expressions.Expr;
import interpreter.functions.PreDefinedFunction;
import interpreter.statements.For;
import interpreter.statements.Statement;
import interpreter.statements.While;
import interpreter.types.ProcedureDefinition;

import java.util.HashSet;
import java.util.Random;

// This class provides environment variables

public class Environment {
    // number of CPUs (cores) in the executing system
    private final   static  int             CORES                       = Runtime.getRuntime().availableProcessors();

    // random number generator
    private         static  Random          randoom                     = null;

    private         static  boolean         sIsInteractive              = false;
    private         static  boolean         sPrintAfterEval             = false;
    private         static  boolean         sPrintVerbose               = false;

    private final   static  String          TAB                         = "\t";
    private final   static  String          ENDL                        = "\n";

    /* -- debugger -- */

    // last source line read/computed
    public          static  int             sourceLine                  = 0;

    private final   static  HashSet<String> breakpoints                 = new HashSet<String>();
    private         static  boolean         breakpointsEnabled          = false; // are any breakpoints set?

    private         static  boolean         debugModeActive             = false;
    private         static  boolean         debugPromptActive           = false;
    private         static  boolean         debugStepNextExpr           = false;
    private         static  boolean         debugStepThroughFunction    = false;
    private         static  boolean         debugFinishFunction         = false;
    private         static  boolean         debugFinishLoop             = false;

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

    public static void setPrintVerbose(boolean printVerbose) {
        sPrintVerbose       = printVerbose;
    }

    public static boolean isPrintVerbose() {
        return sPrintVerbose;
    }

    public static String getLineStart(int tabs) {
        if (!sPrintVerbose || tabs <= 0) {
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

    public static void setBreakpoint(String id) {
        breakpoints.add(id);
        setBreakpointsEnabled(true);
    }

    public static boolean removeBreakpoint(String id) {
        boolean result = breakpoints.remove(id);
        setBreakpointsEnabled(breakpoints.size() > 0);
        return result;
    }

    public static void removeAllBreakpoints() {
        breakpoints.clear();
        setBreakpointsEnabled(false);
    }

    public static boolean isBreakpoint(String id) {
        return breakpoints.contains(id);
    }

    public static String[] getAllBreakpoints() {
        return breakpoints.toArray(new String[0]);
    }

    public static void setBreakpointsEnabled(boolean enabled) {
        breakpointsEnabled       = enabled;
        Call.sBreakpointsEnabled = enabled;
    }

    public static boolean areBreakpointsEnabled() {
        return breakpointsEnabled;
    }

    public static void setDebugModeActive(boolean active) {
        debugModeActive                 = active;
        Statement.sDebugModeActive      = active;
    }

    public static boolean isDebugModeActive() {
        return debugModeActive;
    }

    public static void setDebugPromptActive(boolean active) {
        debugPromptActive = active;
    }

    public static boolean isDebugPromptActive() {
        return debugPromptActive;
    }

    public static void setDebugStepNextExpr(boolean stepNextExpr) {
        debugStepNextExpr   = stepNextExpr;
        Expr.sStepNext      = stepNextExpr;
    }

    public static boolean isDebugStepNextExpr() {
        return debugStepNextExpr;
    }

    public static void setDebugStepThroughFunction(boolean stepThrough) {
        debugStepThroughFunction                    = stepThrough;
        ProcedureDefinition.sStepThroughFunction    = stepThrough;
        PreDefinedFunction.sStepThroughFunction     = stepThrough;
    }

    public static boolean isDebugStepThroughFunction() {
        return debugStepThroughFunction;
    }

    public static void setDebugFinishFunction(boolean finish) {
        debugFinishFunction                 = finish;
        ProcedureDefinition.sFinishFunction = finish;
        Call.sFinishOuterFunction           = finish;
    }

    public static boolean isDebugFinishFunction() {
        return debugFinishFunction;
    }

    public static void setDebugFinishLoop(boolean finish) {
        debugFinishLoop     = finish;
        For.sFinishLoop     = finish;
        While.sFinishLoop   = finish;
    }

    public static boolean isDebugFinishLoop() {
        return debugFinishLoop;
    }
}


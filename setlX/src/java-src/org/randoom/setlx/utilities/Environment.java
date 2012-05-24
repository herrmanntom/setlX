package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.expressions.Call;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.statements.For;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.statements.While;
import org.randoom.setlx.types.ProcedureDefinition;

import java.util.HashSet;
import java.util.Random;

// This class provides environment variables

public class Environment {
    // interface provider to the outer world
    private         static  EnvironmentProvider sEnvProvider                = null;

    // is input feed by a human?
    private         static  boolean             sIsHuman                    = false;

    // random number generator
    private         static  Random              sRandoom                    = null;

    private         static  boolean             sIsInteractive              = false;
    private         static  boolean             sPrintAfterEval             = false;
    private         static  boolean             sPrintVerbose               = false;
    private         static  boolean             sAssertsDisabled            = false;

    /* -- debugger -- */
    private final   static  HashSet<String>     sBreakpoints                = new HashSet<String>();
    private         static  boolean             sBreakpointsEnabled         = false; // are any breakpoints set?

    private         static  boolean             sDebugModeActive            = false;
    private         static  boolean             sDebugPromptActive          = false;
    private         static  boolean             sDebugStepNextExpr          = false;
    private         static  boolean             sDebugStepThroughFunction   = false;
    private         static  boolean             sDebugFinishFunction        = false;
    private         static  boolean             sDebugFinishLoop            = false;

    public static void setEnvironmentProvider(EnvironmentProvider envProvider) {
        sEnvProvider = envProvider;
    }

    public static EnvironmentProvider getEnvironmentProvider() {
        return sEnvProvider;
    }

    /* I/O */

    public static String inReadLine() throws JVMIOException {
        return sEnvProvider.inReadLine();
    }

    // write to standard output
    public static void outWrite(String msg) {
        sEnvProvider.outWrite(msg);
    }
    public static void outWriteLn(String msg) {
        sEnvProvider.outWrite(msg + sEnvProvider.getEndl());
    }
    public static void outWriteLn() {
        sEnvProvider.outWrite(sEnvProvider.getEndl());
    }
    public static void outFlush() {
        sEnvProvider.outFlush();
    }

    // write to standard error
    public static void errWrite(String msg) {
        sEnvProvider.errWrite(msg);
    }
    public static void errWriteLn(String msg) {
        sEnvProvider.errWrite(msg + sEnvProvider.getEndl());
    }
    public static void errWriteLn() {
        sEnvProvider.errWrite(sEnvProvider.getEndl());
    }
    public static void errFlush() {
        sEnvProvider.errFlush();
    }

    public static boolean promptForStdInOnStdOut(String prompt) throws JVMIOException {
        // Only if a pipe is connected the input is ready (has input buffered)
        // BEFORE the prompt.
        // A human usually takes time AFTER the prompt to type something ;-)
        //
        // Also if at one time a prompt was displayed, display all following
        // prompts. (User may continue to type into stdin AFTER we last read
        // from it, causing stdin to be ready, but human controlled)
        if (sIsHuman || ! sEnvProvider.inReady()) {
            sEnvProvider.outWrite(prompt);
            sEnvProvider.outFlush();
            sIsHuman = true;
            return true;
        }
        return false;
    }

    /* other stuff */

    public static int getNumberOfCores() {
        int cores = sEnvProvider.getNumberOfCores();
        if (cores >= 2) {
            return cores;
        } else {
            return 1;
        }
    }

    // current time in ms
    public static long currentTimeMillis() {
        return sEnvProvider.currentTimeMillis();
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

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public static double getRandomDouble() {
        if (sRandoom == null) {
            sRandoom = new Random();
        }
        return sRandoom.nextDouble();
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
        String tab = sEnvProvider.getTab();
        String r   = tab;
        for (int i = 1; i < tabs; i++) {
            r += tab;
        }
        return r;
    }

    public static String getEndl() {
        if (sPrintVerbose) {
            return sEnvProvider.getEndl();
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


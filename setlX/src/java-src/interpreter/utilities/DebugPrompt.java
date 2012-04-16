package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.ParserException;
import interpreter.expressions.Expr;
import interpreter.statements.Block;
import interpreter.statements.Statement;

public class DebugPrompt {

    private static boolean firstPrompt      = true;
    private static boolean continePrompt    = true;
    private static String  currentStatement = "";

    public static void prompt(Expr nextExpr) throws SetlException {
        prompt("Next Expression:\n  " + nextExpr.toString() + "\nIn Statement:\n  " + currentStatement);
    }

    public static void prompt(Statement nextStatement) throws SetlException {
        currentStatement = nextStatement.toString();
        prompt("Next Statement:\n  " + currentStatement);
    }

    public static void prompt(String message) throws SetlException {
        continePrompt = true;
        if (firstPrompt) {
            System.out.println("-~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Debug~Mode~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-");
            System.out.println("Execute helpDgb(); to display debugger commands and their explanation.");
            firstPrompt = false;
        }

        Block   blk      = null;
        do {
            System.out.println(); // newline to visually separate the next input
            System.out.println(message);
            System.out.print("dbg> ");
            System.out.flush();
            try {
                ParseSetlX.resetErrorCount();
                blk         = ParseSetlX.parseInteractive();
            } catch (ParserException pe) {
                System.err.println("\nLast input not executed due to errors in it.");
                blk      = null;
            }
            if (blk != null) {
                try {
                    Environment.setDebugPromptActive(true);
                    blk.execute();
                } finally {
                    Environment.setDebugPromptActive(false);
                }
            }
        } while (continePrompt);
    }

    public static void stopPrompt() {
        continePrompt = false;
    }
}


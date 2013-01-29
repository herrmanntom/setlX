package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Statement;

public class DebugPrompt {

    private static boolean firstPrompt      = true;
    private static boolean continePrompt    = true;
    private static String  currentStatement = "";

    public static void prompt(final State state, final Expr nextExpr) throws SetlException {
        prompt(state, "Next Expression:\n  " + nextExpr.toString() + "\nIn Statement:\n  " + currentStatement);
    }

    public static void prompt(final State state, final Statement nextStatement) throws SetlException {
        currentStatement = nextStatement.toString();
        prompt(state, "Next Statement:\n  " + currentStatement);
    }

    public static void prompt(final State state, final String message) throws SetlException {
        continePrompt = true;
        if (firstPrompt) {
            state.outWriteLn(
                "-~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Debug~Mode~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-"
            );
            state.outWriteLn(
                "Execute dbgHelp(); to display debugger commands and their explanation."
            );
            firstPrompt = false;
        }

        Block   blk      = null;
        do {
            // prompt including newline to visually separate the next input
            state.promptUnchecked("\n" + message + "\ndbg> ");
            try {
                state.resetParserErrorCount();
                blk         = ParseSetlX.parseInteractive(state);
            } catch (final ParserException pe) {
                state.errWriteLn("\nLast input not executed due to errors in it.");
                blk      = null;
            }
            if (blk != null) {
                try {
                    state.setDebugPromptActive(true);
                    blk.exec(state);
                } finally {
                    state.setDebugPromptActive(false);
                }
            }
        } while (continePrompt);
    }

    public static void stopPrompt() {
        continePrompt = false;
    }
}


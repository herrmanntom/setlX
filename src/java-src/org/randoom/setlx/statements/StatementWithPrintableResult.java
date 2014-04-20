package org.randoom.setlx.statements;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Base class for SetlX statements, that have a result that can be printed.
 */
/*package*/ abstract class StatementWithPrintableResult extends Statement {

    /**
     * Enable to print result after execution.
     */
    /*package*/ abstract void setPrintAfterExecution();

    /**
     * Print result value.
     *
     * @param state  Current state of the running setlX program.
     * @param result Result value to print.
     */
    protected void printResult(final State state, final Value result) {
        final StringBuilder out = new StringBuilder();

        if (state.isInteractive()) {
            out.append("~< Result: ");
        }
        result.appendString(state, out, 0);
        if (state.isInteractive()) {
            out.append(" >~");
        }

        state.outWriteLn(out.toString());
    }

}


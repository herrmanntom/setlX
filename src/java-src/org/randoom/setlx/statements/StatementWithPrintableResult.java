package org.randoom.setlx.statements;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/*package*/ abstract class StatementWithPrintableResult extends Statement {

    /*package*/ abstract void setPrintAfterEval();

    /*package*/ void printResult(final State state, final Value result) {
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


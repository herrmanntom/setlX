package org.randoom.setlx.statements;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/*package*/ abstract class StatementWithPrintableResult extends Statement {

    /*package*/ abstract void setPrintAfterEval();

    /*package*/ void printResult(final State state, final Value result) {
        if (state.isInteractive()) {
            state.outWriteLn("~< Result: " + result.toString() + " >~");
        } else {
            state.outWriteLn(result.toString());
        }
    }

}


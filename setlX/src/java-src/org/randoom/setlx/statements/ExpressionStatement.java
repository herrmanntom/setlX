package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | anyExpr ';'
    ;

implemented here as:
      =======
       mExpr
*/

public class ExpressionStatement extends Statement {
    private final Expr    mExpr;
    private       boolean mPrintAfterEval;

    public ExpressionStatement(final Expr expression) {
        mExpr           = expression;
        mPrintAfterEval = false;
    }

    public void setPrintAfterEval() {
        mPrintAfterEval = true;
    }

    protected Value exec() throws SetlException {
        final Value v = mExpr.eval();
        if (mPrintAfterEval && (v != Om.OM || !((Om) v).isHidden()) ) {
            Environment.outWriteLn("~< Result: " + v + " >~");
        }
        return null;
    }

    /* string operations */

    public String toString(final int tabs) {
        return Environment.getLineStart(tabs) + mExpr.toString(tabs) + ";";
    }

    /* term operations */

    public Value toTerm() {
        return mExpr.toTerm();
    }
}


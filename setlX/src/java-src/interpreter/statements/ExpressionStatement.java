package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Value;
import interpreter.utilities.Environment;

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
    private Expr   mExpr;
    private int    mLineNr;

    public ExpressionStatement(Expr expression) {
        mExpr   = expression;
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = ++Environment.sourceLine;
        mExpr.computeLineNr();
    }

    public void execute() throws SetlException {
        Value v = mExpr.eval();
        if (Environment.isPrintAfterEval()) {
            System.out.println("Result: " + v);
        }
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getLineStart(getLineNr(), tabs) + mExpr.toString(tabs) + ";";
    }

    /* term operations */

    public Value toTerm() {
        return mExpr.toTerm();
    }
}


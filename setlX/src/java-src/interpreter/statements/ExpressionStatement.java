package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Om;
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

    public ExpressionStatement(Expr expression) {
        mExpr   = expression;
    }

    public void exec() throws SetlException {
        Value v = mExpr.eval();
        if (Environment.isPrintAfterEval() && (v != Om.OM || !((Om) v).isHidden() )) {
            System.out.println("Result: " + v);
        }
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getLineStart(tabs) + mExpr.toString(tabs) + ";";
    }

    /* term operations */

    public Value toTerm() {
        return mExpr.toTerm();
    }
}


package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class BracketedExpr extends Expr {
    private Expr mExpr;

    public BracketedExpr(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval();
    }

    public String toString(int tabs) {
        return "(" + mExpr.toString(tabs) + ")";
    }
}


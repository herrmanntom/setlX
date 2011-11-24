package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Negative extends Expr {
    private Expr mExpr;

    public Negative(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().negate();
    }

    public String toString(int tabs) {
        return "-" + mExpr.toString(tabs);
    }
}


package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.NumberValue;

public class Abs extends Expr {
    private Expr mExpr;

    public Abs(Expr expr) {
        mExpr = expr;
    }

    public NumberValue evaluate() throws SetlException {
        return mExpr.eval().absoluteValue();
    }

    public String toString() {
        return "abs " + mExpr;
    }
}

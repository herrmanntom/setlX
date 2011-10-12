package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlString;

public class Str extends Expr {
    private Expr mExpr;

    public Str(Expr expr) {
        mExpr = expr;
    }

    public SetlString evaluate() throws SetlException {
        return mExpr.eval().str();
    }

    public String toString() {
        return "str " + mExpr;
    }
}

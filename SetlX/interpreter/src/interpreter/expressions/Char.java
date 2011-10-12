package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlString;

public class Char extends Expr {
    private Expr mExpr;

    public Char(Expr expr) {
        mExpr = expr;
    }

    public SetlString evaluate() throws SetlException {
        return mExpr.eval().charConvert();
    }

    public String toString() {
        return "char " + mExpr;
    }
}

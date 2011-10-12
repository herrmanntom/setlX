package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlSet;

public class Domain extends Expr {
    private Expr mExpr;

    public Domain(Expr expr) {
        mExpr = expr;
    }

    public SetlSet evaluate() throws SetlException {
        return mExpr.eval().domain();
    }

    public String toString() {
        return "domain " + mExpr;
    }
}

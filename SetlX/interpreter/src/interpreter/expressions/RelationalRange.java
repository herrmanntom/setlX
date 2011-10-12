package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlSet;

public class RelationalRange extends Expr {
    private Expr mExpr;

    public RelationalRange(Expr expr) {
        mExpr = expr;
    }

    public SetlSet evaluate() throws SetlException {
        return mExpr.eval().range();
    }

    public String toString() {
        return "range " + mExpr;
    }
}

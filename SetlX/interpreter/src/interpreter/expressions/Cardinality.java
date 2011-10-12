package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlInt;

public class Cardinality extends Expr {
    private Expr mExpr;

    public Cardinality(Expr expr) {
        mExpr = expr;
    }

    public SetlInt evaluate() throws SetlException {
        return mExpr.eval().cardinality();
    }

    public String toString() {
        return "#" + mExpr;
    }
}

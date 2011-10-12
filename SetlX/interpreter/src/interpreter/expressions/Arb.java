package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Arb extends Expr {
    private Expr mExpr;

    public Arb(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().arbitraryMember();
    }

    public String toString() {
        return "arb " + mExpr;
    }
}

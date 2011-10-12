package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Pow extends Expr {
    private Expr mExpr;

    public Pow(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().powerSet();
    }

    public String toString() {
        return "pow " + mExpr;
    }
}

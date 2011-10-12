package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;

public class Negation extends Expr {
    private BoolExpr mExpr;

    public Negation(BoolExpr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        return mExpr.evaluate().not();
    }

    public String toString() {
        return "not " + mExpr;
    }
}

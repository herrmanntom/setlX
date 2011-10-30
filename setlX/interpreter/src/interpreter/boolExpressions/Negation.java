package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;

public class Negation extends Expr {
    private Expr mExpr;

    public Negation(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        return mExpr.eval().not();
    }

    public String toString(int tabs) {
        return "!" + mExpr.toString(tabs);
    }
}


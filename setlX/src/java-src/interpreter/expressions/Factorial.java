package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlInt;

public class Factorial extends Expr {
    private Expr mExpr;

    public Factorial(Expr expr) {
        mExpr = expr;
    }

    public SetlInt evaluate() throws SetlException {
        return mExpr.eval().factorial();
    }

    public String toString(int tabs) {
        return mExpr.toString(tabs) + "!";
    }
}


package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlInt;
import interpreter.types.Value;

public class IsInteger extends Expr {
    private Expr mExpr;

    public IsInteger(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mExpr.eval() instanceof SetlInt) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public String toString() {
        return "is_integer " + mExpr;
    }
}

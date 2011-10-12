package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlReal;
import interpreter.types.Value;

public class IsReal extends Expr {
    private Expr mExpr;

    public IsReal(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mExpr.eval() instanceof SetlReal) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public String toString() {
        return "is_real " + mExpr;
    }
}

package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlSet;
import interpreter.types.Value;

public class IsSet extends Expr {
    private Expr mExpr;

    public IsSet(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mExpr.eval() instanceof SetlSet) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public String toString() {
        return "is_set " + mExpr;
    }
}

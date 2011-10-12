package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlTuple;
import interpreter.types.Value;

public class IsTuple extends Expr {
    private Expr mExpr;

    public IsTuple(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mExpr.eval() instanceof SetlTuple) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public String toString() {
        return "is_tuple " + mExpr;
    }
}

package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlList;
import interpreter.types.Value;

public class IsTuple extends Expr {
    private Expr mExpr;

    public IsTuple(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mExpr.eval() instanceof SetlList) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public String toString() {
        return "is_tuple " + mExpr;
    }
}

package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;

public class Conjunction extends Expr {
    private BoolExpr mLhs;
    private BoolExpr mRhs;

    public Conjunction(BoolExpr lhs, BoolExpr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mLhs.evalToBool() && mRhs.evalToBool()) {
            return SetlBoolean.TRUE;
        }
        return SetlBoolean.FALSE;
    }

    public String toString() {
        return mLhs + " and " + mRhs;
    }
}

package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;

public class Disjunction extends Expr {
    private BoolExpr mLhs;
    private BoolExpr mRhs;

    public Disjunction(BoolExpr lhs, BoolExpr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mLhs.evalToBool() || mRhs.evalToBool()) {
            return SetlBoolean.TRUE;
        }
        return SetlBoolean.FALSE;
    }

    public String toString() {
        return mLhs + " || " + mRhs;
    }
}

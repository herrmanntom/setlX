package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;

public class Implication extends Expr {
    private BoolExpr mLhs;
    private BoolExpr mRhs;

    public Implication(BoolExpr lhs, BoolExpr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        if (mLhs.evalToBool() && ( ! mRhs.evalToBool() ) ) {
            return SetlBoolean.FALSE;
        }
        return SetlBoolean.TRUE;
    }

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " -> " + mRhs.toString(tabs);
    }
}


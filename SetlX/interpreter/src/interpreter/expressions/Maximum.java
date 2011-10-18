package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Value;

public class Maximum extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Maximum(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().maximum(mRhs.eval());
    }

    public String toString() {
        return mLhs + " max " + mRhs;
    }
}


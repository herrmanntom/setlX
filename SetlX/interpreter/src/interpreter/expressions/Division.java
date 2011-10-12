package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Division extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Division(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().divide(mRhs.eval());
    }

    public String toString() {
        return mLhs + " / " + mRhs;
    }
}

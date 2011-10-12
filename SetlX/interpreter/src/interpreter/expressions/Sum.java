package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Sum extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Sum(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().add(mRhs.eval());
    }

    public String toString() {
        return mLhs.toString() + " + " + mRhs.toString();
    }
}

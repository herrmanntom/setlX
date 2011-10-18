package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Difference extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Difference(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().subtract(mRhs.eval());
    }

    public String toString() {
        return mLhs + " - " + mRhs;
    }
}

package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Value;

public class Minimum extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Minimum(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().minimum(mRhs.eval());
    }

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " min " + mRhs.toString(tabs);
    }
}


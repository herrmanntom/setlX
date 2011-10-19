package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.NumberValue;

public class Power extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Power(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public NumberValue evaluate() throws SetlException {
        return mLhs.eval().power(mRhs.eval());
    }

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " ** " + mRhs.toString(tabs);
    }
}


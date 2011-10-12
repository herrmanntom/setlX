package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Product extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Product(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().multiply(mRhs.eval());
    }

    public String toString() {
        return mLhs + " * " + mRhs;
    }
}

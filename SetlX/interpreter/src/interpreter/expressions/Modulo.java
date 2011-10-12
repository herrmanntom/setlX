package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlInt;
import interpreter.types.Value;

public class Modulo extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Modulo(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().mod(mRhs.eval());
    }

    public String toString() {
        return mLhs.toString() + " % " + mRhs.toString();
    }
}

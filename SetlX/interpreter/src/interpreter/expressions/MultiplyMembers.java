package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class MultiplyMembers extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public MultiplyMembers(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        Value result = mRhs.eval().multiplyMembers();
        if (mLhs != null && !(result instanceof SetlOm)) {
            return mLhs.eval().multiply(result);
        } else if (mLhs != null) {
            return mLhs.eval();
        } else {
            return result;
        }
    }

    public String toString() {
        return ((mLhs != null)? mLhs + " ":"") + "*/" + mRhs;
    }
}

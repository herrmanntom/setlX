package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class MaximumMember extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public MaximumMember(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        Value result = mRhs.eval().maximumMember();
        if (mLhs != null) {
            return mLhs.eval().maximum(result);
        } else {
            return result;
        }
    }

    public String toString() {
        return ((mLhs != null)? mLhs + " ":"") + "max/ " + mRhs.toString();
    }
}

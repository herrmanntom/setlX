package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class SumMembers extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public SumMembers(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        Value result = mRhs.eval().addMembers();
        if (mLhs != null && !(result instanceof SetlOm)) {
            return mLhs.eval().add(result);
        } else if (mLhs != null) {
            return mLhs.eval();
        } else {
            return result;
        }
    }

    public String toString(int tabs) {
        return ((mLhs != null)? mLhs.toString(tabs) + " ":"") + "+/" + mRhs.toString(tabs);
    }
}

package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class Assignment extends Expr {
    private AssignmentLhs mLhs;
    private Expr          mRhs;

    public Assignment(AssignmentLhs lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        Value v = mRhs.eval();
        return mLhs.setValue(v);
    }

    public String toString() {
        return mLhs + " := " + mRhs;
    }
}

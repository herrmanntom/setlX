package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class MaximumMember extends Expr {
    private Expr mRhs;

    public MaximumMember(Expr rhs) {
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mRhs.eval().maximumMember();
    }

    public String toString(int tabs) {
        return "max/" + mRhs.toString(tabs);
    }
}


package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class MultiplyMembers extends Expr {
    private Expr mRhs;

    public MultiplyMembers(Expr rhs) {
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mRhs.eval().multiplyMembers();
    }

    public String toString(int tabs) {
        return "*/" + mRhs.toString(tabs);
    }
}


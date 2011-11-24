package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class SumMembers extends Expr {
    private Expr mRhs;

    public SumMembers(Expr rhs) {
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mRhs.eval().addMembers();
    }

    public String toString(int tabs) {
        return "+/" + mRhs.toString(tabs);
    }
}

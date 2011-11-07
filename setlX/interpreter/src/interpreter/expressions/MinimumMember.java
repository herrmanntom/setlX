package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public class MinimumMember extends Expr {
    private Expr mRhs;

    public MinimumMember(Expr rhs) {
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mRhs.eval().minimumMember();
    }

    public String toString(int tabs) {
        return "min/" + mRhs.toString(tabs);
    }
}


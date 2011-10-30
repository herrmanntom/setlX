package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;

public class Implication extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Implication(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        return mLhs.eval().implies(mRhs);
    }

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " -> " + mRhs.toString(tabs);
    }
}


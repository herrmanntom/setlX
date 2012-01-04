package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Term;

/*
grammar rule:
disjunction
    : conjunction ('||' conjunction)*
    ;

implemented here as:
      ===========       ===========
         mLhs              mRhs
*/

public class Disjunction extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Disjunction(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        return mLhs.eval().or(mRhs);
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " || " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'disjunction");
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }
}


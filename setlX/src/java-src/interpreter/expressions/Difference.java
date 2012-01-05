package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
sum
    : product ([...] | '-' product)*
    ;

implemented here as:
      =======              =======
       mLhs                 mRhs
*/

public class Difference extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Difference(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().subtract(mRhs.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " - " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'difference");
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }
}


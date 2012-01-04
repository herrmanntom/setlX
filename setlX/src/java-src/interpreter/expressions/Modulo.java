package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
product
    : power ([...] | '%' power)*
    ;

implemented here as:
      =====              =====
      mLhs               mRhs
*/

public class Modulo extends Expr {
    private Expr mLhs;
    private Expr mRhs;

    public Modulo(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().mod(mRhs.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " % " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'modulo");
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }
}


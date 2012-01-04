package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
power
    : factor ('**' power)?
    ;

implemented here as:
      ======       =====
       mLhs        mExponent
*/

public class Power extends Expr {
    private Expr mLhs;
    private Expr mExponent;

    public Power(Expr lhs, Expr exponent) {
        mLhs        = lhs;
        mExponent   = exponent;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().power(mExponent.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " ** " + mExponent.toString(tabs);
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'power");
        result.addMember(mLhs.toTerm());
        result.addMember(mExponent.toTerm());
        return result;
    }
}


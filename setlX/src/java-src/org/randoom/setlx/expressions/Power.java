package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

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
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^power";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1800;

    private Expr mLhs;
    private Expr mExponent;

    public Power(Expr lhs, Expr exponent) {
        mLhs      = lhs;
        mExponent = exponent;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().power(mExponent.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " ** " + mExponent.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mExponent.toTerm());
        return result;
    }

    public static Power termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs        = TermConverter.valueToExpr(PRECEDENCE, true , term.firstMember());
            Expr exponent   = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new Power(lhs, exponent);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

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
    private final static String FUNCTIONAL_CHARACTER = "'power";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1800;

    private Expr mLhs;
    private Expr mExponent;
    private int  mLineNr;

    public Power(Expr lhs, Expr exponent) {
        mLhs        = lhs;
        mExponent   = exponent;
        mLineNr     = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
        mLhs.computeLineNr();
        mExponent.computeLineNr();
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


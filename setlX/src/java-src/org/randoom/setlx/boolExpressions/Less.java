package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
comparison
    : expr '<' expr
    ;

implemented here as:
      ====     ====
      mLhs     mRhs
*/

public class Less extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "^less";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    private Expr mLhs;
    private Expr mRhs;

    public Less(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    /*
     * Note that these comparisons do not always behave as expected when
     * the compared values are no numbers!
     *
     * For example:
     * Set comparisons are based upon the subset relation
     * a < b
     * is true, when a is a subset of b.
     *
     * However the negation of this comparison
     * !(a < b)
     * is not (in all cases) equal to
     * a >= b
     * because it is possible that a is not a subset of b AND
     * b is neither subset nor equal to a (e.g. a := {1}; b := {2}).
     * In this case
     * !(a < b)
     * would be true, but
     * a >= b
     * would be false.
     *
     * When comparing numbers
     * !(a < b)
     * would always be the same as
     * a >= b
     */

    public SetlBoolean evaluate() throws SetlException {
        return mLhs.eval().isLessThan(mRhs.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " < " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static Less termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true , term.lastMember());
            return new Less(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Term;
import interpreter.utilities.TermConverter;

/*
grammar rule:
comparison
    : expr '>' expr
    ;

implemented here as:
      ====     ====
      mLhs     mRhs
*/

public class More extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "'more";

    private Expr mLhs;
    private Expr mRhs;

    public More(Expr lhs, Expr rhs) {
        mLhs  = lhs;
        mRhs  = rhs;
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
        // note: rhs and lhs swapped!
        return mRhs.eval().isLessThan(mLhs.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " > " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static More termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(term.firstMember());
            Expr rhs = TermConverter.valueToExpr(term.lastMember());
            return new More(lhs, rhs);
        }
    }
}


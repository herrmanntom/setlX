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
    : expr 'notin' expr
    ;

implemented here as:
      ====         ====
      mLhs         mRhs
*/

public class NotIn extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "'notIn";

    private Expr mLhs;
    private Expr mRhs;

    public NotIn(Expr lhs, Expr rhs) {
        mLhs  = lhs;
        mRhs  = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        // note: rhs and lhs swapped!
        return mRhs.eval().containsMember(mLhs.eval()).not();
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " notin " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static NotIn termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(term.firstMember());
            Expr rhs = TermConverter.valueToExpr(term.lastMember());
            return new NotIn(lhs, rhs);
        }
    }
}


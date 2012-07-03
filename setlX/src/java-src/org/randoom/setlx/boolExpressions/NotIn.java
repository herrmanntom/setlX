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
    : expr 'notin' expr
    ;

implemented here as:
      ====         ====
      mLhs         mRhs
*/

public class NotIn extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "^notIn";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    private final Expr mLhs;
    private final Expr mRhs;

    public NotIn(final Expr lhs, final Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    protected SetlBoolean evaluate() throws SetlException {
        // note: rhs and lhs swapped!
        return mRhs.eval().containsMember(mLhs.eval()).not();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mLhs.appendString(sb, tabs);
        sb.append(" notin ");
        mRhs.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static NotIn termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true , term.lastMember());
            return new NotIn(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


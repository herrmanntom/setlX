package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
factor
    : [...]
    | simpleFactor '!'?
    ;

implemented here as:
      ============
         mExpr
*/

public class Factorial extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^factorial";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr mExpr;

    public Factorial(final Expr expr) {
        mExpr = expr;
    }

    protected Value evaluate() throws SetlException {
        return mExpr.eval().factorial();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mExpr.appendString(sb, tabs);
        sb.append("!");
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(mExpr.toTerm());
        return result;
    }

    public static Factorial termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr expr = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            return new Factorial(expr);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
prefixOperation
    : [...]
    | '#' factor
    ;

implemented here as:
          ======
          mExpr
*/

public class Cardinality extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^cardinality";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private Expr mExpr;

    public Cardinality(Expr expr) {
        mExpr   = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().cardinality();
    }

    /* string operations */

    public String toString(int tabs) {
        return "#" + mExpr.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mExpr.toTerm());
        return result;
    }

    public static Cardinality termToExpr(Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr expr = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            return new Cardinality(expr);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
disjunction
    : conjunction ('||' conjunction)*
    ;

implemented here as:
      ===========       ===========
         mLhs              mRhs
*/

public class Disjunction extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^disjunction";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1300;

    private Expr mLhs;
    private Expr mRhs;

    public Disjunction(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public Value evaluate() throws SetlException {
        return mLhs.eval().or(mRhs);
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " || " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static Disjunction termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true , term.lastMember());
            return new Disjunction(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


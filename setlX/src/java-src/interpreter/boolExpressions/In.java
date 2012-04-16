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
    : expr 'in' expr
    ;

implemented here as:
      ====      ====
      mLhs      mRhs
*/

public class In extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "^in";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    private Expr mLhs;
    private Expr mRhs;

    public In(Expr lhs, Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        // note: rhs and lhs swapped!
        return mRhs.eval().containsMember(mLhs.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mLhs.toString(tabs) + " in " + mRhs.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static In termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true,  term.lastMember());
            return new In(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.TermConverter;

/*
grammar rule:
simpleFactor
    : '(' expr ')'
    | [...]
    ;

implemented here as:
          ====
          mExpr
*/

public class BracketedExpr extends Expr {
    // functional character used in terms (MUST be classname starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^bracketedExpr";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private Expr mExpr;

    public BracketedExpr(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval();
    }

    /* string operations */

    public String toString(int tabs) {
        return "(" + mExpr.toString(tabs) + ")";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mExpr.toTerm());
        return result;
    }

    public static BracketedExpr termToExpr(Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr expr = TermConverter.valueToExpr(term.firstMember());
            return new BracketedExpr(expr);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


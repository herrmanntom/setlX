package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

/*
grammar rule:
prefixOperation
    : [...]
    | '@' factor
    ;

implemented here as:
          ======
          mExpr
*/

public class Quote extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^quote";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private Expr mExpr;
    private int  mLineNr;

    public Quote(Expr expr) {
        mExpr   = expr;
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
        mExpr.computeLineNr();
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval();
    }

    /* string operations */

    public String toString(int tabs) {
        return "@" + mExpr.toString(tabs);
    }

    /* term operations */

    public Value toTerm() {
        return mExpr.toTerm();
    }

    public static Quote termToExpr(Term term) throws TermConversionException {
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


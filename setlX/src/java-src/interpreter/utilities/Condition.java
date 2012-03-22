package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
condition
    : boolExpr
    ;

implemented here as:
      ========
       mExpr
*/

public class Condition {
    private Expr mExpr;
    private int  mLineNr;

    public Condition(Expr expr) {
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

    public SetlBoolean eval() throws SetlException {
        Value v = mExpr.eval();
        if (v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) { // is Boolean value?
            return (SetlBoolean) v;
        } else {
            throw new IncompatibleTypeException("'" + v + "' is not a Boolean value.");
        }
    }

    public boolean evalToBool() throws SetlException {
        return eval() == SetlBoolean.TRUE;
    }

    /* string operations */

    public String toString(int tabs) {
        return mExpr.toString(tabs);
    }

    /* term operations */

    public Value toTerm() {
        return mExpr.toTerm();
    }
}


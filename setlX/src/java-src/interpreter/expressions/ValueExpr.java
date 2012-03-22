package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;

// this class wraps values into an expression

public class ValueExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private Value mValue;
    private int   mLineNr;

    public ValueExpr(Value value) {
        mValue  = value;
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
        mValue.computeLineNr();
    }

    public Value eval() {
        return mValue;
    }

    public Value evaluate() {
        return eval();
    }

    /* string operations */

    public String toString(int tabs) {
        return mValue.toString(tabs);
    }

    /* term operations */

    public Value toTerm() {
        return mValue.toTerm();
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


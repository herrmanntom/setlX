package org.randoom.setlx.expressions;

import org.randoom.setlx.types.Value;

// this class wraps values into an expression

public class ValueExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private final Value mValue;

    public ValueExpr(final Value value) {
        mValue  = value;
    }

    public Value eval() {
        return mValue;
    }

    protected Value evaluate() {
        return eval();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mValue.appendString(sb, tabs);
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


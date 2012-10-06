package org.randoom.setlx.expressions;

import org.randoom.setlx.types.Value;

import java.util.List;

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

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        /* nothing to collect */
        mValue.optimize();
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


package org.randoom.setlx.expressions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// this class wraps values into an expression

public class ValueExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private final Value mValue;

    public ValueExpr(final Value value) {
        mValue  = value;
    }

    @Override
    public Value eval(final State state) {
        return mValue;
    }

    @Override
    protected Value evaluate(final State state) {
        return eval(state);
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mValue.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return mValue.toTerm(state);
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


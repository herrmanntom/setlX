package org.randoom.setlx.expressions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;
import java.util.Set;

/**
 * Expression containing a single variable.
 */
public class ValueExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int PRECEDENCE = 9999;

    private final Value value;

    public ValueExpr(final Value value) {
        this.value = value;
    }

    @Override
    public Value eval(final State state) {
        return value;
    }

    @Override
    protected Value evaluate(final State state) {
        return eval(state);
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        value.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return value.toTerm(state);
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    /* Java Code generation */

    @Override
    public void appendJavaCode(
            final State         state,
            final Set<String>   header,
            final StringBuilder code,
            final int           tabs
    ) {
        value.appendJavaCode(state, header, code, tabs);
    }
}


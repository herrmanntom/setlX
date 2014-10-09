package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Expression containing a single value.
 */
public class ValueExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int PRECEDENCE = 9999;

    private final Value value;

    /**
     * Constructor.
     *
     * @param value Contained value.
     */
    public ValueExpr(final Value value) {
        this.value = value;
    }

    @Override
    public Value eval(final State state) {
        return value;
    }

    @Override
    protected Value evaluate(final State state) {
        return value;
    }

    @Override
    protected void collectVariables (
        final State        state,
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
    public Value toTerm(final State state) throws SetlException {
        return value.toTerm(state);
    }

    /* comparisons */

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ValueExpr.class) {
            return value.compareTo(((ValueExpr) other).value);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ValueExpr.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ValueExpr.class) {
            return value.equals(((ValueExpr) obj).value);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        return  ((int) compareToOrdering()) + value.hashCode();
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


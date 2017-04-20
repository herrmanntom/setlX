package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Wrapper class, checking if eval() returns a boolean, used in various statements.
 *
 * grammar rule:
 * condition
 *     : expr
 *     ;

 * implemented here as:
 *       ====
 *       expr
 */
public class Condition extends ImmutableCodeFragment {
    private final OperatorExpression expr;

    /**
     * Create a new Condition.
     *
     * @param expr Expression to evaluate to Boolean result.
     */
    public Condition(final OperatorExpression expr) {
        this.expr = expr;
    }

    /**
     * Evaluate this condition.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlBoolean evaluate(final State state) throws SetlException {
        final Value v = expr.evaluate(state);
        if (v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) { // is Boolean value?
            return (SetlBoolean) v;
        } else {
            throw new IncompatibleTypeException("'" + v + "' is not a Boolean value.");
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        expr.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        return expr.toTerm(state);
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Condition.class) {
            return expr.compareTo(((Condition) other).expr);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Condition.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Condition.class) {
            Condition condition = (Condition) obj;
            return expr.equals(condition.expr);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + expr.hashCode();
    }
}


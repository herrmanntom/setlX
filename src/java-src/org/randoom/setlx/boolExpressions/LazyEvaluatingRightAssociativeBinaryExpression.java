package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.RightAssociativeBinaryExpression;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Generic implementation for expressions with left-hand-side and right-hand-side, which perform lazy evaluation.
 */
public abstract class LazyEvaluatingRightAssociativeBinaryExpression extends RightAssociativeBinaryExpression {

    /**
     * Constructor.
     *
     * @param lhs Left hand side of the expression.
     * @param rhs Right hand side of the expression.
     */
    protected LazyEvaluatingRightAssociativeBinaryExpression(final Expr lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    protected abstract SetlBoolean lhsResultCausingLaziness();

    @Override
    protected final void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        Value value = null;
        if (lhs.isReplaceable()) {
            try {
                value = lhs.eval(state);
            } catch (final Throwable t) {
                value = null;
            }
        }
        if (value == null || value != lhsResultCausingLaziness()) {
            rhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }
}


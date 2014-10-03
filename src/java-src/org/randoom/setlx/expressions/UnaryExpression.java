package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Generic implementation for expressions with one argument.
 */
public abstract class UnaryExpression extends Expr {

    /**
     * Left-hand-side of the expression.
     */
    protected final Expr expr;

    /**
     * Constructor.
     *
     * @param expr argument of the expression.
     */
    protected UnaryExpression(final Expr expr) {
        this.expr = expr;
    }

    @Override
    protected final void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }


    /* term operations */

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    protected abstract String getFunctionalCharacter();

    @Override
    public final Term toTerm(final State state) throws SetlException {
        final Term result = new Term(getFunctionalCharacter(), 1);
        result.addMember(state, expr.toTerm(state));
        return result;
    }

    /* comparisons */

    @Override
    public final int compareTo(final Expr other) {
        if (this == other) {
            return 0;
        } else if (this.getClass() == other.getClass()) {
            return expr.compareTo(((UnaryExpression) other).expr);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            return expr.equals(((UnaryExpression) obj).expr);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return  ((int) compareToOrdering()) + expr.hashCode();
    }
}


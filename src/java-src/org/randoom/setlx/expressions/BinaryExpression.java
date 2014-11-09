package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Generic implementation for expressions with left-hand-side and right-hand-side.
 */
public abstract class BinaryExpression extends Expr {

    /**
     * Left-hand-side of the expression.
     */
    protected final Expr lhs;
    /**
     * Left-hand-side of the expression.
     */
    protected final Expr rhs;

    /**
     * Constructor.
     *
     * @param lhs Left hand side of the expression.
     * @param rhs Right hand side of the expression.
     */
    protected BinaryExpression(final Expr lhs, final Expr rhs) {
        this.lhs = unify(lhs);
        this.rhs = unify(rhs);
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        rhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    /**
     * Appends a string representation of this operator to the given
     * StringBuilder object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param sb StringBuilder to append to.
     */
    public abstract void appendOperator(final StringBuilder sb);

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendBracketedExpr(state, sb, tabs, precedence(), false);
        appendOperator(sb);
        rhs.appendBracketedExpr(state, sb, tabs, precedence(), true);
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
        final Term result = new Term(getFunctionalCharacter(), 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, rhs.toTerm(state));
        return result;
    }

    /* comparisons */

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (this.getClass() == other.getClass()) {
            final BinaryExpression otr = (BinaryExpression) other;
            int cmp = lhs.compareTo(otr.lhs);
            if (cmp != 0) {
                return cmp;
            }
            return rhs.compareTo(otr.rhs);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            final BinaryExpression other = (BinaryExpression) obj;
            return lhs.equals(other.lhs) && rhs.equals(other.rhs);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) compareToOrdering()) + lhs.hashCode();
        hash = hash * 31 + rhs.hashCode();
        return hash;
    }
}


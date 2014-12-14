package org.randoom.setlx.expressions;

import org.randoom.setlx.utilities.State;

/**
 * Generic implementation for right-associative expressions with left-hand-side and right-hand-side.
 */
public abstract class RightAssociativeBinaryExpression extends BinaryExpression {

    /**
     * Constructor.
     *
     * @param lhs Left hand side of the expression.
     * @param rhs Right hand side of the expression.
     */
    protected RightAssociativeBinaryExpression(final Expr lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendBracketedExpr(state, sb, tabs, precedence(), true);
        appendOperator(sb);
        rhs.appendBracketedExpr(state, sb, tabs, precedence(), false);
    }
}


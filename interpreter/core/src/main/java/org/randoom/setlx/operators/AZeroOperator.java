package org.randoom.setlx.operators;

/**
 * Base class for operators that take no arguments.
 * They are in fact not really operators.
 */
public abstract class AZeroOperator extends AOperator {
    @Override
    public boolean hasArgumentBeforeOperator() {
        return false;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return false;
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 9999;
    }
}

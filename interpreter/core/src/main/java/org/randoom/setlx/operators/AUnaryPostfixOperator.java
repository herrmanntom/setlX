package org.randoom.setlx.operators;

/**
 * Base class for unary postfix operators.
 */
public abstract class AUnaryPostfixOperator extends AOperator {
    @Override
    public boolean hasArgumentBeforeOperator() {
        return false;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return true;
    }
}

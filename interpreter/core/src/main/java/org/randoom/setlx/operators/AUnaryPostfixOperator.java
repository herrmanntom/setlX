package org.randoom.setlx.operators;

/**
 * Base class for unary postfix operators.
 */
public abstract class AUnaryPostfixOperator extends AUnaryOperator {

    @Override
    public boolean hasArgumentBeforeOperator() {
        return true;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return false;
    }
}

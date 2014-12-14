package org.randoom.setlx.operators;

/**
 * Base class for binary infix operators.
 */
public abstract class ABinaryInfixOperator extends AOperator {
    @Override
    public boolean hasArgumentBeforeOperator() {
        return true;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return true;
    }
}

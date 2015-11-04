package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Operator that raises the second value from the stack by the first one.
 */
public class Power extends ABinaryInfixOperator {
    /** Singleton **/
    public static final Power P = new Power();

    private Power() {}

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        return lhs.power(state, rhs);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" ** ");
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public boolean isRightAssociative() {
        return true;
    }

    @Override
    public int precedence() {
        return 2000;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Power.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

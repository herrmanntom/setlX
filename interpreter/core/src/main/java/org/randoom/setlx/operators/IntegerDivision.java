package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Operator that computes the integer division of two values from the stack.
 */
public class IntegerDivision extends ABinaryInfixOperator {
    /** Singleton **/
    public static final IntegerDivision ID = new IntegerDivision();

    private IntegerDivision() {}

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        return lhs.integerDivision(state, rhs);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" \\ ");
    }

    @Override
    public boolean isLeftAssociative() {
        return true;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 1700;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(IntegerDivision.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

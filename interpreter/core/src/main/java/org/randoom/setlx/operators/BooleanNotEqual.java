package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Operator that checks if two values on the stack are not equal.
 */
public class BooleanNotEqual extends ABinaryInfixOperator {
    /** Singleton **/
    public static final BooleanNotEqual BNE = new BooleanNotEqual();

    private BooleanNotEqual() {}

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        try {
            return lhs.isEqualTo(state, rhs).not(state);
        } catch (final SetlException se) {
            se.addToTrace("Error in substitute comparison \"!(" + lhs.toString(state) + " == " + rhs.toString(state) + ")\":");
            throw se;
        }
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" <!=> ");
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
        return 1100;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(BooleanNotEqual.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

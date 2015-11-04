package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Operator that checks if the second values on the stack is greater then the first or equal to it.
 */
public class GreaterOrEqual extends ABinaryInfixOperator {
    /** Singleton **/
    public static final GreaterOrEqual GOE = new GreaterOrEqual();

    private GreaterOrEqual() {}

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        try {
            // note: rhs and lhs swapped!
            return SetlBoolean.valueOf(rhs.isEqualTo(state, lhs) == SetlBoolean.TRUE || rhs.isLessThan(state, lhs) == SetlBoolean.TRUE);
        } catch (final SetlException se) {
            se.addToTrace("Error in substitute comparison \"(" + rhs.toString(state) + " == " + lhs.toString(state) + ") || (" + rhs.toString(state) + " < " + lhs.toString(state) +  ")\":");
            throw se;
        }
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" >= ");
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
        return 1500;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(GreaterOrEqual.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

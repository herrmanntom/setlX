package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Operator that computes the modulo of two values from the stack.
 */
public class Modulo extends ABinaryInfixOperator {
    /** Singleton **/
    public static final Modulo M = new Modulo();

    private Modulo() {}

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        return lhs.modulo(state, rhs);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" % ");
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

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Modulo.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

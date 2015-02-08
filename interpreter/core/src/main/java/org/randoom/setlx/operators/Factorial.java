package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * Operator that computes the factorial and puts the result on the stack.
 */
public class Factorial extends AUnaryPostfixOperator {

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        return values.poll().factorial(state);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append("!");
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
        return 2100;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Factorial.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other || other.getClass() == Factorial.class) {
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj.getClass() == Factorial.class;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

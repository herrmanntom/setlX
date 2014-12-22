package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Operator that computes the difference of two values on the stack.
 */
public class Difference extends ABinaryInfixOperator {
    @Override
    public Value evaluate(State state, ValueStack values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        return lhs.difference(state, rhs);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" - ");
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
        return 1600;
    }
}

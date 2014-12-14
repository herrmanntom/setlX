package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.utilities.State;

/**
 * Operator that inverts one value on the stack.
 */
public class Minus extends AUnaryPostfixOperator {
    @Override
    public void evaluate(State state, ValueStack values) throws SetlException {
        values.push(values.poll().minus(state));
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append("-");
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
        return 1900;
    }
}

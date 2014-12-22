package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * A simple operator that puts a value on the stack.
 */
public class ValueOperator extends AZeroOperator {

    private final Value value;

    /**
     * Constructor.
     *
     * @param value Contained value.
     */
    public ValueOperator(final Value value) {
        this.value = value;
    }

    @Override
    public Value evaluate(State state, ValueStack values) throws SetlException {
        return value;
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        value.appendString(state, sb, 0);
    }
}

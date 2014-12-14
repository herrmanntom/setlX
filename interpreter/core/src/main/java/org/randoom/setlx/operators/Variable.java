package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.utilities.State;

/**
 * Operator that gets a variable from the current scope and puts it on the stack.
 */
public class Variable extends AZeroOperator {

    private final String id;

    /**
     * Create a new Variable expression.
     *
     * @param id ID/name of this variable.
     */
    public Variable(final String id) {
        this.id = id;
    }

    @Override
    public void evaluate(State state, ValueStack values) throws SetlException {
        values.push(state.findValue(id));
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(id);
    }
}

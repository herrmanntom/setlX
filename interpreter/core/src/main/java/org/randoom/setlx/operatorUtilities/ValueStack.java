package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.types.Value;

import java.util.LinkedList;

/**
 * A stack of values used when evaluating expressions of operators.
 */
public class ValueStack {
    private LinkedList<Value> values;

    /**
     * Create a new value stack.
     */
    public ValueStack() {
        values = new LinkedList<Value>();
    }

    /**
     * Get number of values on the stack.
     *
     * @return number of values on the stack.
     */
    public int size() {
        return values.size();
    }

    /**
     * Put one value on the stack.
     *
     * @param value to put on the stack.
     */
    /*package*/ void push(Value value) {
        values.push(value);
    }

    /**
     * Get a value from the stack and remove it there.
     *
     * @return value from the stack.
     * @throws IllegalStateException if the stack is empty.
     */
    public Value poll() throws IllegalStateException {
        Value value = values.poll();
        if (value == null) {
            throw new IllegalStateException("Error in operator stack evaluation! Stack is empty");
        } else {
            return value;
        }
    }
}

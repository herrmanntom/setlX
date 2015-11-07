package org.randoom.setlx.operatorUtilities;

import java.util.LinkedList;

/**
 * A stack of values used when evaluating expressions of operators.
 */
public class Stack<T> {
    private LinkedList<T> values;

    /**
     * Create a new value stack.
     */
    public Stack() {
        values = new LinkedList<T>();
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
    /*package*/ void push(T value) {
        values.push(value);
    }

    /**
     * Get a value from the stack and remove it there.
     *
     * @return value from the stack.
     * @throws IllegalStateException if the stack is empty.
     */
    public T poll() throws IllegalStateException {
        T value = values.poll();
        if (value == null) {
            throw new IllegalStateException("Error in operator stack evaluation! Stack is empty");
        } else {
            return value;
        }
    }
}

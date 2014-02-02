package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.State;

/**
 * The setlX data type for encapsulating errors caught in try-catch.
 */
public class SetlError extends Value {

    private final String message;

    /**
     * Create a new SetlError containing the specified message.
     *
     * @param message Error message.
     */
    public SetlError(final String message) {
        this.message = message;
    }

    /**
     * Create a new SetlError containing the message of the specified exception.
     *
     * @param exception Exception to copy the message from.
     */
    public SetlError(final SetlException exception) {
        this(exception.getMessage());
    }

    @Override
    public SetlError clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isError() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(message);
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlError) {
            final SetlError error = (SetlError) v;
            return message.compareTo(error.message);
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    protected int compareToOrdering() {
        return 100;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlError) {
            return message.equals(((SetlError) v).message);
        } else {
            return false;
        }
    }

    private final static int initHashCode = SetlError.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode * 31 + message.hashCode();
    }
}


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.State;

public class SetlError extends Value {

    private final String mMessage;

    public SetlError(final String message) {
        mMessage = message;
    }

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
        sb.append(mMessage);
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlError) {
            final SetlError error = (SetlError) v;
            return mMessage.compareTo(error.mMessage);
        } else {
            // everything is bigger
            return 1;
        }
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlError) {
            return mMessage.equals(((SetlError) v).mMessage);
        } else {
            return false;
        }
    }

    private final static int initHashCode = SetlError.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode * 31 + mMessage.hashCode();
    }
}


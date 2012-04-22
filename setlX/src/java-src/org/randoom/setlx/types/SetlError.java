package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;

public class SetlError extends Value {

    private String mMessage;

    public SetlError(String message){
        mMessage = message;
    }

    public SetlError(SetlException exception){
        this(exception.getMessage());
    }

    public SetlError clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isError() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    public SetlString str() {
        return new SetlString(toString());
    }

    public String toString() {
        return "Error: " + mMessage;
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
    public int compareTo(Value v){
        if (v instanceof SetlError) {
            SetlError error = (SetlError) v;
            return mMessage.compareTo(error.mMessage);
        } else {
            // everything is bigger
            return 1;
        }
    }
}


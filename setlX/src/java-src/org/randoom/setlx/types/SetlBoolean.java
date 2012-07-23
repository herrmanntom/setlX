package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;

public class SetlBoolean extends Value {

    public final static SetlBoolean FALSE = new SetlBoolean();
    public final static SetlBoolean TRUE  = new SetlBoolean();

    private SetlBoolean() {  }

    public SetlBoolean clone() {
        // this value is atomic and can not be changed once set
        return this;
    }

    public static SetlBoolean valueOf(final boolean value){
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /* Boolean operations */

    public Value and(final Expr other) throws SetlException {
        if (this == FALSE) {
            return FALSE;
        } else { // this == TRUE
            final Value otr = other.eval();
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else if (otr instanceof Term) {
                return ((Term) otr).andFlipped(this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this + " && " + otr + "' is not a Boolean value."
                );
            }
        }
    }

    public Value implies(final Expr other) throws SetlException {
        if (this == FALSE) {
            return TRUE;
        } else { // this == TRUE
            final Value otr = other.eval();
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else if (otr instanceof Term) {
                return ((Term) otr).impliesFlipped(this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this + " => " + otr + "' is not a Boolean value."
                );
            }
        }
    }

    public SetlBoolean not() {
        if (this == TRUE) {
            return FALSE;
        } else {
            return TRUE;
        }
    }

    public Value or(final Expr other) throws SetlException {
        if (this == TRUE) {
            return TRUE;
        } else { // this == FALSE
            final Value otr = other.eval();
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
                return FALSE;
            } else if (otr instanceof Term) {
                return ((Term) otr).orFlipped(this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this + " || " + otr + "' is not a Boolean value."
                );
            }
        }
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isBoolean() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        if (this == TRUE) {
            sb.append("true");
        } else {
            sb.append("false");
        }
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
    // also FALSE < TRUE
    public int compareTo(final Value v){
        if (this == v) {
            // as only exacly one FALSE and TRUE object exist, we can compare by reference
            return 0;
        } else if (v instanceof SetlError || v == Om.OM ||
                   v == Infinity.NEGATIVE || (this == TRUE && v == FALSE)) {
            // only SetlError, Om, -Infinity and FALSE are smaller
            return 1;
        } else {
            // everything else is bigger
            return -1;
        }
    }

    public int hashCode() {
        if (this == TRUE) {
            return 2015404846;
        } else {
            return -117843451;
        }
    }
}


package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;

public class SetlBoolean extends Value {

    public final static SetlBoolean FALSE = new SetlBoolean();
    public final static SetlBoolean TRUE  = new SetlBoolean();

    private SetlBoolean() {  }

    public SetlBoolean clone() {
        // this value is atomic and can not be changed once set
        return this;
    }

    public static SetlBoolean get(boolean value){
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /* Boolean operations */

    public SetlBoolean and(Expr other) throws SetlException {
        if (this == FALSE) {
            return FALSE;
        } else { // this == TRUE
            Value otr = other.eval();
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else {
                throw new IncompatibleTypeException("Right-hand-side of '" + this + " && " + otr + "' is not a Boolean value.");
            }
        }
    }

    public SetlBoolean implies(Expr other) throws SetlException {
        if (this == FALSE) {
            return TRUE;
        } else { // this == TRUE
            Value otr = other.eval();
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else {
                throw new IncompatibleTypeException("Right-hand-side of '" + this + " -> " + otr + "' is not a Boolean value.");
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

    public SetlBoolean or(Expr other) throws SetlException {
        if (this == TRUE) {
            return TRUE;
        } else { // this == FALSE
            Value otr = other.eval();
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
                return FALSE;
            } else {
                throw new IncompatibleTypeException("Right-hand-side of '" + this + " || " + otr + "' is not a Boolean value.");
            }
        }
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isBoolean() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    public String toString() {
        if (this == TRUE) {
            return "true";
        } else {
            return "false";
        }
    }

    /* comparisons */

    /* Compare two Values.  Returns -1 if this value is less than the value given
     * as argument, +1 if its greater and 0 if both values contain the same
     * elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    // also FALSE < TRUE
    public int compareTo(Value v){
        if (this == v) {
            // as only exacly one FALSE and TRUE object exist, we can compare by reference
            return 0;
        } else if (v == Om.OM || v == Infinity.NEGATIVE || (this == TRUE && v == FALSE)) {
            // only Om, -Infinity and FALSE are smaller
            return 1;
        } else {
            // everything else is bigger
            return -1;
        }
    }
}

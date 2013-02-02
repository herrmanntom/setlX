package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.utilities.State;

public class SetlBoolean extends Value {

    public final static SetlBoolean FALSE = new SetlBoolean();
    public final static SetlBoolean TRUE  = new SetlBoolean();

    private SetlBoolean() {  }

    @Override
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

    @Override
    public Value conjunction(final State state, final Expr other) throws SetlException {
        if (this == FALSE) {
            return FALSE;
        } else { // this == TRUE
            final Value otr = other.eval(state);
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else if (otr instanceof Term) {
                return ((Term) otr).conjunctionFlipped(state, this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this + " && " + otr + "' is not a Boolean value."
                );
            }
        }
    }

    @Override
    public Value disjunction(final State state, final Expr other) throws SetlException {
        if (this == TRUE) {
            return TRUE;
        } else { // this == FALSE
            final Value otr = other.eval(state);
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
                return FALSE;
            } else if (otr instanceof Term) {
                return ((Term) otr).disjunctionFlipped(state, this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this + " || " + otr + "' is not a Boolean value."
                );
            }
        }
    }

    @Override
    public Value implication(final State state, final Expr other) throws SetlException {
        if (this == FALSE) {
            return TRUE;
        } else { // this == TRUE
            final Value otr = other.eval(state);
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else if (otr instanceof Term) {
                return ((Term) otr).implicationFlipped(state, this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this + " => " + otr + "' is not a Boolean value."
                );
            }
        }
    }

    @Override
    public SetlBoolean negation(final State state) {
        if (this == TRUE) {
            return FALSE;
        } else {
            return TRUE;
        }
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isBoolean() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
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
     */
    // also FALSE < TRUE
    @Override
    public int compareTo(final Value v){
        if (this == v) {
            // as only exacly one FALSE and TRUE object exist, we can compare by reference
            return 0;
        } else if (this == TRUE  && v == FALSE) {
            return 1;
        } else if (this == FALSE && v == TRUE ) {
            return -1;
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    /* To compare "incomparable" values, e.g. of different types, the following
     * order is established and used in compareTo():
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    protected int compareToOrdering() {
        return 400;
    }

    @Override
    public boolean equalTo(final Value v){
        if (this == v) {
            // as only exactly one FALSE and TRUE object exist, we can compare by reference
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this == TRUE) {
            return 2015404846;
        } else {
            return -117843451;
        }
    }
}


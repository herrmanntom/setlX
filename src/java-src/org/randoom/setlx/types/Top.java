package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

public class Top extends Value {
    public final static Top TOP = new Top();

    private Top() {}

    @Override
    public Top clone() {
        // this value is atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of Boolean operation) */
    @Override
    public SetlBoolean isTop() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
	sb.append("Top");
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else {
            return 1;
        }
    }

    /* To compare "incomparable" values, e.g. of different types, the following
     * order is established and used in compareTo():
     * SetlError < Om < SetlBoolean < Rational & SetlDouble
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < Top
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    protected int compareToOrdering() {
	return 1300;
    }

    @Override
    public boolean equalTo(final Value v) {
        // as only exactly one object of class Top ever exists, we can get away with comparing the reference
        return this == v;
    }

    @Override
    public int hashCode() {
        return -1029009190;
    }
}


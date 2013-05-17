package org.randoom.setlx.types;

import org.randoom.setlx.utilities.State;

public class Om extends Value {
    // functional character used in terms
    public final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Om.class);

    public final static Om     OM = new Om();

    private Om() {  }

    @Override
    public Om clone() {
        // this value is atomic and can not be changed
        return this;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("om");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v){
        if (v == OM) {
            return 0;
        }  else {
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
        return 200;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (v == OM) {
            return true;
        } else {
            return false;
        }
    }

    private final static int initHashCode = Om.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }
}


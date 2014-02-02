package org.randoom.setlx.types;

import org.randoom.setlx.utilities.State;

/**
 * This class represents an undefined value.
 */
public class Om extends Value {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Om.class);

    /**
     * Singleton of the one and only undefined value in setlX.
     */
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

    @Override
    public int compareTo(final Value v){
        if (v == OM) {
            return 0;
        }  else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

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

    /**
     * Get the functional character of this value type used in terms.
     *
     * @return Functional character of this value type.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


package org.randoom.setlx.types;

import org.randoom.setlx.utilities.State;

/**
 * Representation of the largest value-type as far as compareTo is concerned.
 *
 * For internal use only.
 */
public class Top extends Value {
    /**
     * The largest value far as compareTo is concerned.
     */
    public final static Top TOP = new Top();

    private Top() {}

    @Override
    public Top clone() {
        // this value is atomic and can not be changed once set
        return this;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("Top");
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int compareToOrdering() {
        return 1700;
    }

    @Override
    public boolean equalTo(final Object v) {
        // as only exactly one object of class Top ever exists, we can get away with comparing the reference
        return this == v;
    }

    private final static int initHashCode = Top.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }
}


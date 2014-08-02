package org.randoom.setlx.types;

import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * This class represents the value of the 'ignore' expression '-'.
 */
public class IgnoreDummy extends Value {

    /**
     * Singleton of the one and only IgnoreDummy.
     */
    public final static IgnoreDummy ID = new IgnoreDummy();

    private IgnoreDummy() {}

    @Override
    public IgnoreDummy clone() {
        // this value is atomic and can not be changed
        return this;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("_");
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(final State state, final Value other) {
        return new MatchResult(true);
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (v == ID) {
            return 0;
        } else {
            return -1; // dummy is uncomparable to anything else
        }
    }

    @Override
    public int compareToOrdering() {
        return 0;
    }

    @Override
    public boolean equalTo(final Object v) {
        return v == ID;
    }

    private final static int initHashCode = IgnoreDummy.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }
}

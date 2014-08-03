package org.randoom.setlx.types;

import org.randoom.setlx.utilities.State;

/**
 * This class represents the value of the 'range' expression '..'.
 */
public class RangeDummy extends Value {

    /**
     * Singleton of the one and only RangeDummy.
     */
    public final static RangeDummy RD = new RangeDummy();

    private RangeDummy(){}

    @Override
    public RangeDummy clone() {
        // this value is atomic and can not be changed
        return this;
    }

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" .. ");
    }

    @Override
    public int compareTo(final Value other) {
        if (other == RD) {
            return 0;
        } else {
            return -1; // dummy is incomparable to anything else
        }
    }

    @Override
    public int compareToOrdering() {
        return 0;
    }

    @Override
    public boolean equalTo(final Object other) {
        return other == RD;
    }

    private final static int initHashCode = RangeDummy.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }
}

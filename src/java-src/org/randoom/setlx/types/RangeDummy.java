package org.randoom.setlx.types;

import org.randoom.setlx.utilities.State;

public class RangeDummy extends Value {

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
    public int compareTo(final Value v) {
        if (v == RD) {
            return 0;
        } else {
            return -1; // dummy is incomparable to anything else
        }
    }

    @Override
    public boolean equalTo(final Value v) {
        if (v == RD) {
            return true;
        } else {
            return false;
        }
    }

    private final static int initHashCode = RangeDummy.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }
}

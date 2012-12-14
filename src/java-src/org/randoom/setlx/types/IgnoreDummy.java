package org.randoom.setlx.types;

import org.randoom.setlx.utilities.MatchResult;

public class IgnoreDummy extends Value {

    public final static IgnoreDummy ID = new IgnoreDummy();

    private IgnoreDummy() {}

    public IgnoreDummy clone() {
        // this value is atomic and can not be changed
        return this;
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("_");
    }

    /* term operations */

    public MatchResult matchesTerm(final Value other) {
        return new MatchResult(true);
    }

    /* comparisons */

    public int compareTo(final Value v) {
        if (v == ID) {
            return 0;
        } else {
            return -1; // dummy is uncomparable to anything else
        }
    }

    public boolean equalTo(final Value v) {
        if (v == ID) {
            return true;
        } else {
            return false;
        }
    }

    private final static int initHashCode = IgnoreDummy.class.hashCode();

    public int hashCode() {
        return initHashCode;
    }
}

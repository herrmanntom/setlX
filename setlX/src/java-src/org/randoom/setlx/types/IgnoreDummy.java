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

    public String toString() {
        return "_";
    }

    /* term operations */

    public MatchResult matchesTerm(Value other) {
        return new MatchResult(true);
    }

    /* comparisons */

    public int compareTo(Value v) {
        if (v == ID) {
            return 0;
        } else {
            return -1; // dummy is uncomparable to anything else
        }
    }
}

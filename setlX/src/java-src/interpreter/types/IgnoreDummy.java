package interpreter.types;

import interpreter.utilities.MatchResult;

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
        return -1; // dummy is uncomparable, but throwable interface does not handle exceptions...
    }
}

package org.randoom.setlx.types;

public class RangeDummy extends Value {

    public final static RangeDummy RD = new RangeDummy();

    private RangeDummy(){}

    public RangeDummy clone() {
        // this value is atomic and can not be changed
        return this;
    }

    public String toString() {
        return " .. ";
    }

    public int compareTo(final Value v) {
        if (v == RD) {
            return 0;
        } else {
            return -1; // dummy is uncomparable to anything else
        }
    }
}

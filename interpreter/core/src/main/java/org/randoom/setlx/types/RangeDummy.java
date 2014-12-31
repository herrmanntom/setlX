package org.randoom.setlx.types;

import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * This class represents the value of the 'range' expression '..'.
 */
public class RangeDummy extends ImmutableValue {

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
    public int compareTo(final CodeFragment other) {
        if (other == RD) {
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(RangeDummy.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        return other == RD;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT);
    }
}

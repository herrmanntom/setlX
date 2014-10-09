package org.randoom.setlx.types;

import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * This class represents the value of the 'ignore' expression '-'.
 */
public class IgnoreDummy extends ImmutableValue {

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
    public int compareTo(final CodeFragment other) {
        if (other == ID) {
            return 0;
        }
        return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(IgnoreDummy.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        return other == ID;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}

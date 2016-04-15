package org.randoom.setlx.types;

import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

/**
 * This class represents an undefined value.
 */
public class Om extends ImmutableValue {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Om.class);

    /**
     * Singleton of the one and only undefined value in setlX.
     */
    public final static Om     OM = new Om();

    private Om() {  }

    @Override
    public Om clone() {
        // this value is atomic and can not be changed
        return this;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("om");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (other == OM) {
            return 0;
        }  else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Om.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        return other == OM;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT);
    }

    /**
     * Get the functional character of this value type used in terms.
     *
     * @return Functional character of this value type.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


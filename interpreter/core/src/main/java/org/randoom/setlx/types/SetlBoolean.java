package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * The setlX string data type.
 */
public class SetlBoolean extends ImmutableValue {

    /**
     * SetlBoolean value of false.
     */
    public final static SetlBoolean FALSE = new SetlBoolean();
    /**
     * SetlBoolean value of true.
     */
    public final static SetlBoolean TRUE  = new SetlBoolean();

    private SetlBoolean() {  }

    @Override
    public SetlBoolean clone() {
        // this value is atomic and can not be changed once set
        return this;
    }

    /**
     * Get SetlBoolean representing the specified value.
     *
     * @param value Boolean value to represent.
     * @return      SetlBoolean representation.
     */
    public static SetlBoolean valueOf(final boolean value){
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /* Boolean operations */

    @Override
    public Value conjunction(final State state, final OperatorExpression other) throws SetlException {
        if (this == FALSE) {
            return FALSE;
        } else { // this == TRUE
            final Value otr = other.evaluate(state);
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else if (otr.getClass() == Term.class) {
                return ((Term) otr).conjunctionFlipped(state, this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this.toString(state) + " && " + otr.toString(state) + "' is not a Boolean value."
                );
            }
        }
    }

    @Override
    public Value disjunction(final State state, final OperatorExpression other) throws SetlException {
        if (this == TRUE) {
            return TRUE;
        } else { // this == FALSE
            final Value otr = other.evaluate(state);
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
                return FALSE;
            } else if (otr.getClass() == Term.class) {
                return ((Term) otr).disjunctionFlipped(state, this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this.toString(state) + " || " + otr.toString(state) + "' is not a Boolean value."
                );
            }
        }
    }

    @Override
    public Value implication(final State state, final OperatorExpression other) throws SetlException {
        if (this == FALSE) {
            return TRUE;
        } else { // this == TRUE
            final Value otr = other.evaluate(state);
            if (otr == TRUE) {
                return TRUE;
            } else if (otr == FALSE) {
               return FALSE;
            } else if (otr.getClass() == Term.class) {
                return ((Term) otr).implicationFlipped(state, this);
            } else {
                throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this.toString(state) + " => " + otr.toString(state) + "' is not a Boolean value."
                );
            }
        }
    }

    @Override
    public SetlBoolean not(final State state) {
        if (this == TRUE) {
            return FALSE;
        } else {
            return TRUE;
        }
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isBoolean() {
        return SetlBoolean.TRUE;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (this == TRUE) {
            sb.append("true");
        } else {
            sb.append("false");
        }
    }

    /* comparisons */

    // FALSE < TRUE
    @Override
    public int compareTo(final CodeFragment other){
        if (this == other) {
            // as only exacly one FALSE and TRUE object exist, we can compare by reference
            return 0;
        } else if (this == TRUE  && other == FALSE) {
            return 1;
        } else if (this == FALSE && other == TRUE ) {
            return -1;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlBoolean.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other){
        // as only exactly one FALSE and TRUE object exist, we can compare by reference
        return this == other;
    }

    @Override
    public int computeHashCode() {
        if (this == TRUE) {
            return ((int) COMPARE_TO_ORDER_CONSTANT);
        } else {
            return ((int) COMPARE_TO_ORDER_CONSTANT) - 1;
        }
    }
}


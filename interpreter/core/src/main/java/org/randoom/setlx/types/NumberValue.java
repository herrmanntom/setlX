package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.util.Comparator;

/**
 * This base class provides some functionality for all numeric values.
 */
public abstract class NumberValue extends ImmutableValue {

    public static final Comparator<Value> NUMERICAL_COMPARATOR = new NumericalComparator();

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isNumber() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    @Override
    public final SetlDouble toDouble(final State state) throws NumberToLargeException {
        return toDouble();
    }

    /**
     * Convert this value into a setlX double.
     *
     * @return                        Equivalent double of this value.
     * @throws NumberToLargeException if this value cannot be converted.
     */
    public abstract SetlDouble toDouble() throws NumberToLargeException;

    @Override
    public Rational toRational(final State state) {
        return toRational();
    }

    /**
     * Convert this SetlDouble into a Rational
     *
     * @return Rational number representing this value.
     */
    public abstract Rational toRational();

    /* arithmetic operations */

    @Override
    public abstract NumberValue absoluteValue(final State state);

    @Override
    public abstract NumberValue ceil(final State state) throws UndefinedOperationException;

    @Override
    public abstract Value       difference(final State state, final Value subtrahend) throws SetlException;

    @Override
    public abstract NumberValue floor(final State state) throws UndefinedOperationException;

    @Override
    public abstract NumberValue minus(final State state) throws SetlException;

    @Override
    public          Value       power(final State state, final Value exponent) throws SetlException {
        if (exponent.isInteger() == SetlBoolean.TRUE && exponent.jIntConvertible()) {
            return this.power(state, exponent.jIntValue());
        } else if (exponent.isRational() == SetlBoolean.TRUE) {
            return this.power(state, exponent.jDoubleValue());
        } else if (exponent.isDouble() == SetlBoolean.TRUE) {
            return this.power(state, exponent.jDoubleValue());
        } else if (exponent.getClass() == SetlSet.class && this.equalTo(Rational.TWO)) {
            return ((SetlSet) exponent).powerSet(state);
        } else if (exponent.getClass() == Term.class) {
            return ((Term) exponent).powerFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Exponent of '" + this.toString(state) + " ** " + exponent.toString(state) + "' is not a number."
            );
        }
    }

    /**
     * Raise this value to the power of another.
     *
     * @param state          Current state of the running setlX program.
     * @param exponent       Value to raise by.
     * @return               This raised by the power of exponent.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    protected abstract NumberValue power(final State state, final int    exponent) throws SetlException;
    /**
     * Raise this value to the power of another.
     *
     * @param state          Current state of the running setlX program.
     * @param exponent       Value to raise by.
     * @return               This raised by the power of exponent.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    protected abstract NumberValue power(final State state, final double exponent) throws SetlException;

    @Override
    public    abstract Value       product(final State state, final Value multiplier) throws SetlException;

    @Override
    public    abstract Value       quotient(final State state, final Value divisor) throws SetlException;

    @Override
    public    abstract NumberValue round(final State state) throws SetlException;

    @Override
    public    abstract Value       sum(final State state, final Value summand) throws SetlException;

    /* comparisons */

    @Override
    public final SetlBoolean isLessThan(final State state, final Value other) throws IncompatibleTypeException {
        if (this == other) {
            return SetlBoolean.FALSE;
        } else if (other.isNumber() == SetlBoolean.TRUE) {
            return SetlBoolean.valueOf(this.numericalComparisonTo((NumberValue) other) < 0);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " < " + other.toString(state) + "' is not a number."
            );
        }
    }

    @Override
    public final boolean equalTo(final Object other) {
        return other instanceof NumberValue && this.numericalComparisonTo((NumberValue) other) == 0;
    }

    protected abstract int numericalComparisonTo(final NumberValue other);

    @Override
    public abstract boolean equals(final Object o);

    private static class NumericalComparator implements Comparator<Value> {
        @Override
        public int compare(Value o1, Value o2) {
            if (o1.getClass() != o2.getClass() && o1.isNumber() == SetlBoolean.TRUE && o2.isNumber() == SetlBoolean.TRUE) {
                int c = ((NumberValue) o1).numericalComparisonTo((NumberValue) o2);
                if (c == 0) {
                    return (o1.compareToOrdering() < o2.compareToOrdering())? -1 : 1;
                } else {
                    return c;
                }
            }
            return o1.compareTo(o2);
        }
    }

}


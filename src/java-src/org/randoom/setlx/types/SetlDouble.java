package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * This class represents a binary floating point number.
 */
public class SetlDouble extends NumberValue {
    /**
     * Flag to define how to format doubles when printing them.
     */
    public enum DoublePrintMode {
        /**
         * Flag for printing doubles with the default way of displaying the exponent.
         */
        DEFAULT,
        /**
         * Flag for printing doubles with always displaying the exponent.
         */
        SCIENTIFIC,
        /**
         * Flag for printing doubles with always displaying a exponent which is a multiple of 3.
         */
        ENGINEERING,
        /**
         * Flag for printing doubles without displaying the exponent.
         */
        PLAIN
    }

    private final static DecimalFormat PLAIN_FORMAT = new DecimalFormat("#.#");
    static {
        PLAIN_FORMAT.setGroupingUsed(false);
        PLAIN_FORMAT.setMaximumFractionDigits(340);
        PLAIN_FORMAT.setMaximumIntegerDigits(309);
    }

    private final double doubleValue;

    /**
     * Double value of positive infinity.
     */
    public  final static SetlDouble POSITIVE_INFINITY = SetlDouble.valueOfNoEx(Double.POSITIVE_INFINITY);
    /**
     * Double value of negative infinity.
     */
    public  final static SetlDouble NEGATIVE_INFINITY = SetlDouble.valueOfNoEx(Double.NEGATIVE_INFINITY);
    /**
     * Double value of Euler's number, i.e. lim_n->oo (1+1/n)^n
     */
    public  final static SetlDouble E                 = SetlDouble.valueOfNoEx(Math.E);
    /**
     * Double value of pi, i.e. the ratio of the circumference of a circle to its diameter.
     */
    public  final static SetlDouble PI                = SetlDouble.valueOfNoEx(Math.PI);

    private SetlDouble(final Double d) {
        this.doubleValue = d;
    }

    /**
     * Create a new SetlDouble from a String.
     *
     * @param str                          String to parse as double.
     * @return                             The new SetlDouble.
     * @throws UndefinedOperationException Thrown in case the double is not a number.
     * @throws NumberFormatException       Thrown in case the string does not represent a double.
     */
    public static SetlDouble valueOf(final String str) throws UndefinedOperationException {
        return valueOf(new Double(str));
    }

    /**
     * Create a new SetlDouble from a double.
     *
     * @param  real                        Double value of the new SetlDouble.
     * @return                             The new SetlDouble.
     * @throws UndefinedOperationException Thrown in case the double is not a number.
     */
    public static SetlDouble valueOf(final double real) throws UndefinedOperationException {
        if (Double.isNaN(real)) {
            final String msg = "Result of this operation is undefined/not a number.";
            throw new UndefinedOperationException(msg);
        }
        return new SetlDouble(real);
    }
    // Only use this function if you are sure that real != NAN!
    private static SetlDouble valueOfNoEx(final double real) {
         return new SetlDouble(real);
    }

    /**
     * Create a new SetlDouble from a rational of two BigIntegers.
     *
     * @param  nominator                   Nominator value of the rational.
     * @param  denominator                 Denominator value of the rational.
     * @return                             The new SetlDouble.
     * @throws NumberToLargeException      Thrown in case the BigDecimal is too large or small.
     */
    public static SetlDouble valueOf(final BigInteger nominator, final BigInteger denominator)
        throws NumberToLargeException
    {
        BigInteger nom   = nominator;
        BigInteger denom = denominator;
        double n = nom  .doubleValue();
        double d = denom.doubleValue();
        double r = n / d;
        try {
            // not the most efficient way to do it
            while (Double.isInfinite(n) || Double.isInfinite(d) || Double.isNaN(r)) {
                nom   = denom  .shiftRight(1);
                denom = denom.shiftRight(1);
                n = nom  .doubleValue();
                d = denom.doubleValue();
                r = n / d;
            }
            return new SetlDouble(r);
        } catch (final ArithmeticException ae) {
            throw new NumberToLargeException(
                "The value of " + nominator + "/" + denominator + " is too large or too small for this operation."
            );
        }
    }

    @Override
    public SetlDouble clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of Boolean operation) */
    @Override
    public SetlBoolean isDouble() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */
    @Override
    public Rational toInteger(final State state) throws UndefinedOperationException {
        if (this.doubleValue >= 0.0) {
            return floor(state);
        } else {
            return ceil(state);
        }
    }

    /* Convert an IEEE 754 double to a rational.  The IEEE format specifies
       that bit number 63 is the sign bit, the bits in position 62 to 52
       represent a biased exponent.  The bias is 1023.  The bits from position
       51 down to position 0 are the mantissa.  Note that, as long as the numbers
       are normalized, the mantissa is extended with a 1 bit at position 53.
       A number is denormnalized if the biased exponent has the value 0.  In that
       case, the bias only has the value 1022.
     */
    @Override
    public Rational toRational(final State state)
        throws UndefinedOperationException
    {
        return toRational();
    }

    /**
     * Convert this SetlDouble into a Rational
     *
     * @return Rational number representing this value.
     */
    /*package*/ Rational toRational() {
        final long bits         = Double.doubleToLongBits(this.doubleValue);
        final long signMask     = 0x8000000000000000L;
        final long exponentMask = 0x7ff0000000000000L;
        final long valueMask    = 0x000fffffffffffffL;
        final long biasedExp    = ((bits & exponentMask) >>> 52);
        final boolean sign      = ((bits & signMask) == signMask);
        BigInteger nominator   = null;
        BigInteger denominator = null;
        if (biasedExp == 0) {  // denormalized number
            final long exponent = - 1022 - 52;
            final long mantissa = bits & valueMask;
            if (sign) {
                nominator = BigInteger.valueOf(-mantissa);
            } else {
                nominator = BigInteger.valueOf(mantissa);
            }
            denominator = BigInteger.valueOf(1).shiftLeft((int) -exponent);
            return Rational.valueOf(nominator, denominator);
        } else if (biasedExp < 2047) {  // normalized number
            final long exponent = biasedExp - 1023 - 52;
            final long mantissa = (1L << 52) | (bits & valueMask);
            if (sign) {
                nominator = BigInteger.valueOf(-mantissa);
            } else {
                nominator = BigInteger.valueOf(mantissa);
            }
            if (exponent < 0) {
                denominator = BigInteger.valueOf(1).shiftLeft((int) -exponent);
                return Rational.valueOf(nominator, denominator);
            } else {
                nominator = nominator.shiftLeft((int) exponent);
                return Rational.valueOf(nominator);
            }
        } else { // not a number (NaN) -> Should be impossible, as that is checked in valueOf
            final String msg = "This is not a number (NaN).";
            // throw unchecked exception
            throw new NumberFormatException(msg);
        }
    }

    /**
     * Get the maximum value of a SetlDouble as a Rational.
     *
     * @return Maximum value of a SetlDouble as a Rational.
     */
    /*package*/ static Rational bigRational() {
        final SetlDouble big = new SetlDouble(Double.MAX_VALUE);
        return big.toRational();
    }

    /**
     * Get the minimal value of a SetlDouble as a Rational.
     *
     * @return minimal value of a SetlDouble as a Rational.
     */
    /*package*/ static Rational smallRational() {
        final SetlDouble small = new SetlDouble(Double.MIN_VALUE);
        return small.toRational();
    }

    @Override
    public SetlDouble toDouble(final State state) {
        return this;
    }

    /* native type checks */
    @Override
    public boolean jDoubleConvertible() {
        return true;
    }

    /* native type conversions */
    @Override
    public double jDoubleValue() {
        return this.doubleValue;
    }
    @Override
    public double toJDoubleValue(final State state) {
        return this.doubleValue;
    }

    /* arithmetic operations */
    @Override
    public SetlDouble absoluteValue(final State state) {
        return new SetlDouble(Math.abs(this.doubleValue));
    }

    @Override
    public Rational ceil(final State state) throws UndefinedOperationException {
        return valueOf(Math.ceil(this.doubleValue)).toRational();
    }

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend.getClass() == SetlDouble.class) {
            final SetlDouble rhs = (SetlDouble) subtrahend;
            return SetlDouble.valueOf(this.doubleValue - rhs.jDoubleValue());
        }
        if (subtrahend.getClass() == Rational.class) {
            final Rational rhs = (Rational) subtrahend;
            return SetlDouble.valueOf(this.doubleValue - rhs.toDouble().doubleValue);
         } else if (subtrahend.getClass() == Term.class) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " - " + subtrahend.toString(state) + "' is not a number."
            );
        }
    }

    /**
     * Compute the difference between another value and this.
     *
     * @param state          Current state of the running setlX program.
     * @param minuend        Value to subtract from.
     * @return               Difference of minuend and this.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ Value differenceFlipped(final State state, final Rational minuend) throws SetlException {
        final BigInteger n   = minuend.getNominatorValue();
        final BigInteger d   = minuend.getDenominatorValue();
        final SetlDouble lhs = SetlDouble.valueOf(n, d);
        return lhs.difference(state, this);
    }

    @Override
    public Rational floor(final State state)
        throws UndefinedOperationException
    {
        return valueOf(Math.floor(this.doubleValue)).toRational();
    }

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor.getClass() == SetlDouble.class) {
            final SetlDouble rhs = (SetlDouble) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.jDoubleValue()).floor(state);
        }
        if (divisor.getClass() ==  Rational.class) {
            final Rational rhs = (Rational) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.toDouble().doubleValue).floor(state);
        }
        if (divisor.getClass() == Term.class) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " \\ " + divisor.toString(state) + "' is not a number."
            );
        }
    }

    @Override
    public NumberValue minus(final State state) throws UndefinedOperationException {
        return SetlDouble.valueOf(-this.doubleValue);
    }

    @Override
    protected SetlDouble power(final State state, final int exponent)
        throws UndefinedOperationException
    {
        return SetlDouble.valueOf(Math.pow(this.doubleValue, exponent));
    }

    @Override
    protected NumberValue power(final State state, final double exponent)
        throws UndefinedOperationException
    {
        return SetlDouble.valueOf(Math.pow(this.doubleValue, exponent));
    }

    @Override
    public Value product(final State state, final Value multiplier)
        throws SetlException
    {
        if (multiplier.getClass() == SetlDouble.class) {
            final SetlDouble rhs = (SetlDouble) multiplier;
            return SetlDouble.valueOf(this.doubleValue * rhs.jDoubleValue());
        } else if (multiplier.getClass() == Rational.class) {
            final Rational rhs = (Rational) multiplier;
            return SetlDouble.valueOf(this.doubleValue * rhs.toDouble().doubleValue);
        } else if (multiplier.getClass() == SetlMatrix.class ||
                   multiplier.getClass() == SetlVector.class
        ) {
            return multiplier.product(state, this);
        } else if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " * " + multiplier.toString(state) + "' is not a number."
            );
        }
    }

    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor.getClass() == SetlDouble.class) {
            final SetlDouble rhs = (SetlDouble) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.jDoubleValue());
        }
        if (divisor.getClass() ==  Rational.class) {
            final Rational rhs = (Rational) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.toDouble().doubleValue);
        } else if (divisor.getClass() == Term.class) {
            return ((Term) divisor).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " / " + divisor.toString(state) + "' is not a number."
            );
        }
    }

    /**
     * Divide another value by this.
     *
     * @param state          Current state of the running setlX program.
     * @param dividend       Value to divide by this.
     * @return               Division of dividend and this.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ Value quotientFlipped(final State state, final Rational dividend) throws SetlException {
        final BigInteger n   = dividend.getNominatorValue();
        final BigInteger d   = dividend.getDenominatorValue();
        final SetlDouble lhs = SetlDouble.valueOf(n, d);
        return lhs.quotient(state, this);
    }

    @Override
    public Rational round(final State state) throws UndefinedOperationException {
        return SetlDouble.valueOf(Math.floor(this.doubleValue + 0.5)).toRational(state);
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == SetlDouble.class) {
            final SetlDouble rhs = (SetlDouble) summand;
            return SetlDouble.valueOf(this.doubleValue + rhs.jDoubleValue());
        }
        if (summand.getClass() == Rational.class) {
            final Rational rhs = (Rational) summand;
            return SetlDouble.valueOf(this.doubleValue + rhs.toDouble().doubleValue);
        } else if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " + " + summand.toString(state) + "' is not a number or string."
            );
        }
    }

    @Override
    public SetlBoolean isInfinite() {
        if (this.doubleValue == Double.POSITIVE_INFINITY ||
            this.doubleValue == Double.NEGATIVE_INFINITY   )
        {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    /* string and char operations */

    @Override
    @SuppressWarnings("fallthrough")
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        switch (state.doublePrintMode) {
            case SCIENTIFIC:
                if ( ! Double.isInfinite(doubleValue)) {
                    sb.append(String.format("%e", this.doubleValue));
                    break;
                } // else: fall-through

            case ENGINEERING:
                if ( ! Double.isInfinite(doubleValue)) {
                    double val = this.doubleValue;

                    // If the value is negative, make it positive so the log10 works
                    final double posVal = (val<0) ? -val : val;
                    final double log10  = Math.log10(posVal);

                    // Determine how many orders of 3 magnitudes the value is
                    final int count = (int) Math.floor(log10 / 3);

                    // Scale the value into the range 1<=val<1000
                    val /= Math.pow(10, count * 3);

                    // If no prefix exists just make a string of the form 000e000
                    sb.append(String.format("%.6fe%d", val, count * 3));

                    break;
                } // else: fall-through

            case PLAIN:
                if ( ! Double.isInfinite(doubleValue)) {
                    sb.append(PLAIN_FORMAT.format(this.doubleValue));
                    break;
                } // else: fall-through

            case DEFAULT:
            default:
                sb.append(String.valueOf(this.doubleValue));
                break;
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v.getClass() == SetlDouble.class) {
            final SetlDouble rhs = (SetlDouble) v;
            final Double d = this.doubleValue;
            return d.compareTo(rhs.doubleValue);
        } else if (v.getClass() == Rational.class) {
            try {
                final Double d = this.doubleValue;
                return d.compareTo(((Rational)v).toDouble().doubleValue);
            } catch (final NumberToLargeException e) {
                return toRational().compareTo(v);
            }
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    public int compareToOrdering() {
        return 500;
    }

    @Override
    public boolean equalTo(final Object o) {
        if (o instanceof Value) {
            final Value v = (Value) o;
            if (v.isNumber() == SetlBoolean.TRUE) {
                return this.compareTo(v) == 0;
            }
        }
        return false;
    }

    private final static int initHashCode = SetlDouble.class.hashCode();

    @Override
    public int hashCode() {
        final Double d = this.doubleValue;
        return initHashCode + d.hashCode();
    }
}

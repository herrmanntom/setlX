package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.utilities.State;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class represents a binary floating point number.
 */
public class SetlDouble extends NumberValue {
    public  final static int        PRINT_MODE_DEFAULT     = 1;
    public  final static int        PRINT_MODE_ENGINEERING = 2;
    public  final static int        PRINT_MODE_PLAIN       = 3;

    private final double doubleValue;

    private final static BigDecimal DOUBLE_MAX_VALUE  = BigDecimal.valueOf(Double.MAX_VALUE);
    private final static BigDecimal DOUBLE_MIN_VALUE  = BigDecimal.valueOf(Double.MIN_VALUE);
    public  final static SetlDouble POSITIVE_INFINITY = SetlDouble.valueOfNoEx(Double.POSITIVE_INFINITY);
    public  final static SetlDouble NEGATIVE_INFINITY = SetlDouble.valueOfNoEx(Double.NEGATIVE_INFINITY);

    private SetlDouble(Double d) {
        this.doubleValue = d;
    }
    private SetlDouble(final String s) {
        this.doubleValue = new Double(s);
    }
    private SetlDouble(final BigDecimal real) {
        this.doubleValue = real.doubleValue();
    }
    private SetlDouble(BigInteger nominator, BigInteger denominator) {
        double n = nominator  .doubleValue();
        double d = denominator.doubleValue();
        // not the most efficient way to do it
        while (Double.isInfinite(n) || Double.isInfinite(d)) {
            nominator   = nominator  .shiftRight(1);
            denominator = denominator.shiftRight(1);
            n = nominator  .doubleValue();
            d = denominator.doubleValue();
        }
        this.doubleValue = n / d;
    }

    public static SetlDouble valueOf(final String str) {
        return new SetlDouble(str);
    }
    public static SetlDouble valueOf(final double real) throws UndefinedOperationException {
        if (Double.isNaN(real)) {
            String msg = "Result of this operation is undefined/not a number.";
            throw new UndefinedOperationException(msg);
        }
        return new SetlDouble(real);
    }
    // Only use this function if you are sure that real != NAN!
    static SetlDouble valueOfNoEx(final double real) {
         return new SetlDouble(real);
    }
    public static NumberValue valueOf(final BigDecimal real)
        throws UndefinedOperationException, NumberToLargeException
    {
        final BigDecimal absValue = real.abs();
        if (absValue.compareTo(DOUBLE_MAX_VALUE) > 0 ||
             (
               absValue.compareTo(DOUBLE_MIN_VALUE) < 0 &&
               real.compareTo(BigDecimal.ZERO) != 0
             )
           )
        {
            throw new NumberToLargeException(
                "The value of " + real + " is too large or too small for this operation."
            );
        }
        Double value = real.doubleValue();
        if (Double.isNaN(value)) {
            String msg = "Result of this operation is undefined/not a number.";
            throw new UndefinedOperationException(msg);
        }
        return new SetlDouble(value);
    }
    public static SetlDouble valueOf(final BigInteger nominator, final BigInteger denominator) {
        return new SetlDouble(nominator, denominator);
    }

    @Override
    public SetlDouble clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }
    public Double getDoubleValue() {
        return this.doubleValue;
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
            SetlDouble result = new SetlDouble(Math.floor(this.doubleValue));
            return result.toRational(state);
        } else {
            SetlDouble result = new SetlDouble(Math.ceil(this.doubleValue));
            return result.toRational(state);
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

    private Rational toRational() throws UndefinedOperationException
    {
        long bits         = Double.doubleToLongBits(this.doubleValue);
        long signMask     = 0x8000000000000000L;
        long exponentMask = 0x7ff0000000000000L;
        long valueMask    = 0x000fffffffffffffL;
        long biasedExp    = ((bits & exponentMask) >>> 52);
        boolean sign      = ((bits & signMask) == signMask);
        BigInteger nominator   = null;
        BigInteger denominator = null;
        if (biasedExp == 0) {  // denormalized number
            long exponent = - 1022 - 52;
            long mantissa = bits & valueMask;
            if (sign) {
                nominator = BigInteger.valueOf(-mantissa);
            } else {
                nominator = BigInteger.valueOf(mantissa);
            }
            denominator = BigInteger.valueOf(1).shiftLeft((int) -exponent);
            return Rational.valueOf(nominator, denominator);
        } else if (biasedExp < 2047) {  // normalized number
            long exponent = biasedExp - 1023 - 52;
            long mantissa = (1L << 52) | (bits & valueMask);
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
        } else { // not a number (NaN)
            String msg = "This is not a number (NaN).";
            throw new UndefinedOperationException(msg);
        }
    }

    public static Rational bigRational() {
        try {
            SetlDouble big = new SetlDouble(Double.MAX_VALUE);
            return big.toRational();
        } catch (UndefinedOperationException e) {
            // impossible
        }
        return null;
    }
    public static Rational smallRational() {
        try {
            SetlDouble small = new SetlDouble(Double.MIN_VALUE);
            return small.toRational();
        } catch (UndefinedOperationException e) {
            // impossible
        }
        return null;
    }

    @Override
    public SetlDouble toDouble(final State state) {
        return this;
    }

    /* native type checks */
    @Override
    public boolean jDoubleConvertable() {
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
        SetlDouble result = new SetlDouble(Math.ceil(this.doubleValue));
        return result.toRational(state);
    }

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof SetlDouble) {
            SetlDouble rhs = (SetlDouble) subtrahend;
            return SetlDouble.valueOf(this.doubleValue - rhs.getDoubleValue());
        }
        if (subtrahend instanceof Rational) {
            Rational rhs = (Rational) subtrahend;
            return SetlDouble.valueOf(this.doubleValue - rhs.toDouble().doubleValue);
         } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a number."
            );
        }
    }

    public Value differenceFlipped(final State state, final Rational minuend) throws SetlException {
        BigInteger n   = minuend.getNominatorValue();
        BigInteger d   = minuend.getDenominatorValue();
        SetlDouble lhs = SetlDouble.valueOf(n, d);
        return lhs.difference(state, this);
    }

    @Override
    public Rational floor(final State state)
        throws UndefinedOperationException
    {
        // TODO: this can be done faster
        SetlDouble result = SetlDouble.valueOf(Math.floor(this.doubleValue));
        return result.toRational(state);
    }

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof SetlDouble) {
            SetlDouble rhs = (SetlDouble) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.getDoubleValue()).floor(state);
        }
        if (divisor instanceof Rational) {
            final Rational rhs = (Rational) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.toDouble().doubleValue).floor(state);
        }
        if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " \\ " + divisor + "' is not a number."
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
        throws IncompatibleTypeException, UndefinedOperationException
    {
        if (multiplier instanceof SetlDouble) {
            SetlDouble rhs = (SetlDouble) multiplier;
            return SetlDouble.valueOf(this.doubleValue * rhs.getDoubleValue());
        }
        if (multiplier instanceof Rational) {
            final Rational rhs = (Rational) multiplier;
            return SetlDouble.valueOf(this.doubleValue * rhs.toDouble().doubleValue);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a number."
            );
        }
    }

    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof SetlDouble) {
            SetlDouble rhs = (SetlDouble) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.getDoubleValue());
        }
        if (divisor instanceof Rational) {
            Rational rhs = (Rational) divisor;
            return SetlDouble.valueOf(this.doubleValue / rhs.toDouble().doubleValue);
        } else if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " / " + divisor + "' is not a number."
            );
        }
    }
    public Value quotientFlipped(final State state, final Rational dividend) throws SetlException {
        BigInteger n   = dividend.getNominatorValue();
        BigInteger d   = dividend.getDenominatorValue();
        SetlDouble lhs = SetlDouble.valueOf(n, d);
        return lhs.quotient(state, this);
    }

    @Override
    public Rational round(final State state) throws UndefinedOperationException {
        SetlDouble result = SetlDouble.valueOf(Math.floor(this.doubleValue + 0.5));
        return result.toRational(state);
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof SetlDouble) {
            SetlDouble rhs = (SetlDouble) summand;
            return SetlDouble.valueOf(this.doubleValue + rhs.getDoubleValue());
        }
        if (summand instanceof Rational) {
            Rational rhs = (Rational) summand;
            return SetlDouble.valueOf(this.doubleValue + rhs.toDouble().doubleValue);
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a number or string."
            );
        }
    }

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
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        Double d = this.doubleValue;
        sb.append(d.toString());
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlDouble) {
            final SetlDouble rhs = (SetlDouble) v;
            final Double d = this.doubleValue;
            return d.compareTo(rhs.doubleValue);
        } else if (v instanceof Rational) {
            final Rational rhs = (Rational) v;
            final Double d = this.doubleValue;
            return d.compareTo(rhs.toDouble().doubleValue);
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    protected int compareToOrdering() {
        return 500;
    }

    @Override
    public boolean equalTo(final Value v) {
        return this.compareTo(v) == 0;
    }

    private final static int initHashCode = SetlDouble.class.hashCode();

    @Override
    public int hashCode() {
        Double d = this.doubleValue;
        return initHashCode + d.hashCode();
    }
}


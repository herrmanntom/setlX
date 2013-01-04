package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Real extends NumberValue {

    private static MathContext mathContext = MathContext.DECIMAL64;

    public static void setPrecision32() { // rather stupid
        mathContext = MathContext.DECIMAL32;
    }
    public static void setPrecision64() {
        mathContext = MathContext.DECIMAL64;
    }
    public static void setPrecision128() { // rather crazy
        mathContext = MathContext.DECIMAL128;
    }
    public static void setPrecision256() { // don't ask, don't tell
        mathContext = new MathContext(70, RoundingMode.HALF_EVEN);
    }

    private final BigDecimal mReal;

    private Real(final String s) {
        this(new BigDecimal(s, mathContext));
    }

    private Real(final BigDecimal real) {
        mReal = new BigDecimal(real.toString(), mathContext);
    }

    private Real(final BigInteger nominator, final BigInteger denominator) {
        BigDecimal n = new BigDecimal(nominator,   mathContext);
        BigDecimal d = new BigDecimal(denominator, mathContext);
        mReal = n.divide(d, mathContext);
    }

    public static Real valueOf(final String str) {
        return new Real(str);
    }

    public static NumberValue valueOf(final double real) throws UndefinedOperationException {
        if (Double.isInfinite(real)) {
            if (real > 0) {
                return Infinity.POSITIVE;
            } else {
                return Infinity.NEGATIVE;
            }
        } else if (Double.isNaN(real)) {
            throw new UndefinedOperationException(
                "Result of this operation is undefined/not a number."
            );
        }
        return new Real(Double.toString(real));
    }

    public static Real valueOf(final BigDecimal real) {
        return new Real(real);
    }

    public static Real valueOf(final BigInteger nominator, final BigInteger denominator) {
        return new Real(nominator, denominator);
    }

    public Real clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public BigDecimal getBigDecimalValue() {
        return mReal;
    }

    public double jDoubleValue() throws NumberToLargeException {
        if ( mReal.abs().compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0 ||
             (
               mReal.abs().compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) < 0 &&
               mReal.compareTo(BigDecimal.ZERO) != 0
             )
           )
        {
            throw new NumberToLargeException(
                "The value of " + mReal + " is too large or to small for this operation."
            );
        } else {
            return mReal.doubleValue();
        }
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isReal() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    public Rational toInteger() {
        return Rational.valueOf(mReal.toBigInteger());
    }

    public Rational toRational() {
        final int scale = mReal.scale();
        if (scale >= 0) {
            return Rational.valueOf(mReal.unscaledValue(), BigInteger.TEN.pow(scale));
        } else /* (scale < 0) */ { // real is in fact an integer
            return Rational.valueOf(mReal.unscaledValue().multiply(BigInteger.TEN.pow(scale * -1)));
        }
    }

    public Real toReal() {
        return this;
    }

    /* arithmetic operations */

    public Real absoluteValue() {
        return new Real(mReal.abs());
    }

    public Rational ceil() {
        BigInteger intValue = mReal.toBigInteger();
        if (mReal.compareTo(new BigDecimal(intValue)) == 0 || mReal.compareTo(BigDecimal.ZERO) < 0) {
            return Rational.valueOf(intValue);
        } else /* if (mReal.compareTo(BigDecimal.ZERO) > 0) */ {
            return Rational.valueOf(intValue.add(BigInteger.ONE));
        }
    }

    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof NumberValue) {
            if (subtrahend == Infinity.POSITIVE || subtrahend == Infinity.NEGATIVE) {
                return (Infinity) subtrahend.minus(state);
            }
            BigDecimal right = null;
            if (subtrahend instanceof Real) {
                right = ((Real) subtrahend).mReal;
            } else {
                Rational s = ((Rational) subtrahend);
                right = s.toReal().mReal;
            }
            try {
                return Real.valueOf(mReal.subtract(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " - " + subtrahend);
            }
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a number."
            );
        }
    }

    public Value differenceFlipped(final State state, final Rational minuend) throws SetlException {
        return minuend.toReal().difference(state, this);
    }

    public Rational floor() {
        BigInteger intValue = mReal.toBigInteger();
        if (mReal.compareTo(new BigDecimal(intValue)) == 0 || mReal.compareTo(BigDecimal.ZERO) > 0) {
            return Rational.valueOf(intValue);
        } else /* if (mReal.compareTo(BigDecimal.ZERO) < 0) */ {
            return Rational.valueOf(intValue.subtract(BigInteger.ONE));
        }
    }

    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            return this.quotient(state, divisor).floor();
        } else if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " \\ " + divisor + "' is not a number."
            );
        }
    }

    public NumberValue minus(final State state) throws UndefinedOperationException {
        try {
            return new Real(mReal.negate(mathContext));
        } catch (final ArithmeticException ae) {
            return handleArithmeticException(ae, "-" + this);
        }
    }

    protected NumberValue power(final int exponent) throws UndefinedOperationException {
        try {
            return Real.valueOf(mReal.pow(exponent, mathContext));
        } catch (final ArithmeticException ae) {
            return handleArithmeticException(ae, this + " ** " + exponent);
        }
    }

    protected NumberValue power(final double exponent) throws NumberToLargeException, IncompatibleTypeException, UndefinedOperationException {
        if (mReal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IncompatibleTypeException(
                "Left-hand-side of '" + this + " ** " + exponent + "' is negative."
            );
        }
        final double a = jDoubleValue(); // may throw exception

        // a ** exponent = exp(ln(a ** exponent) = exp(exponent * ln(a))
        return Real.valueOf(Math.exp(exponent * Math.log(a)));
    }

    public Value product(final State state, final Value multiplier) throws IncompatibleTypeException, UndefinedOperationException {
        if (multiplier instanceof NumberValue) {
            if (multiplier == Infinity.POSITIVE || multiplier == Infinity.NEGATIVE) {
                return (Infinity) multiplier;
            }
            BigDecimal right = null;
            if (multiplier instanceof Real) {
                right = ((Real) multiplier).mReal;
            } else {
                Rational m = (Rational) multiplier;
                right = m.toReal().mReal;
            }
            try {
                return Real.valueOf(mReal.multiply(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " * " + multiplier);
            }
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a number."
            );
        }
    }

    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            BigDecimal right = null;
            if (divisor == Infinity.POSITIVE) {
                return new Real("0.0");
            } else if (divisor == Infinity.NEGATIVE) {
                return new Real("-0.0");
            } else if (divisor instanceof Real) {
                right = ((Real) divisor).mReal;
            } else {
                right = ((Rational) divisor).toReal().mReal;
            }
            if (right.compareTo(BigDecimal.ZERO) == 0) {
                final int cmp = this.compareTo(Rational.ZERO);
                if (cmp > 0) {
                    return Infinity.POSITIVE;
                } else if (cmp < 0) {
                    return Infinity.NEGATIVE;
                } else {
                    throw new UndefinedOperationException(
                        "'" + this + " / " + divisor + "' is undefined."
                    );
                }
            }
            try {
                return Real.valueOf(mReal.divide(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " / " + divisor);
            }
        } else if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " / " + divisor + "' is not a number."
            );
        }
    }

    public Value quotientFlipped(final State state, final Rational dividend) throws SetlException {
        return dividend.toReal().quotient(state, this);
    }

    public Rational round(final State state) {
        return Rational.valueOf(mReal.setScale(0, mathContext.getRoundingMode()).toBigInteger());
    }

    public Value sum(final State state, final Value summand) throws IncompatibleTypeException, UndefinedOperationException {
        if (summand instanceof NumberValue) {
            if (summand == Infinity.POSITIVE || summand == Infinity.NEGATIVE) {
                return (Infinity) summand;
            }
            BigDecimal right = null;
            if (summand instanceof Real) {
                right = ((Real) summand).mReal;
            } else {
                Rational s = (Rational) summand;
                right = s.toReal().mReal;
            }
            try {
                return Real.valueOf(mReal.add(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " + " + summand);
            }
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a number or string."
            );
        }
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(mReal.toString());
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof Real) {
            final Real nr = (Real) v;
            return mReal.compareTo(nr.mReal);
        } else if (v instanceof Rational) {
            final Rational nr = (Rational) v;
            return mReal.compareTo(nr.toReal().mReal);
        } else if (v instanceof SetlError || v == Om.OM || v == Infinity.NEGATIVE ||
                   v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) {
            // only SetlError, Om, -Infinity and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }

    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof Real) {
            return mReal.compareTo(((Real) v).mReal) == 0;
        } else if (v instanceof Rational) {
            return mReal.compareTo(((Rational) v).toReal().mReal) == 0;
        } else {
            return false;
        }
    }

    private final static int initHashCode = Real.class.hashCode();

    public int hashCode() {
        return initHashCode + mReal.hashCode();
    }

    /* private */

    public Infinity handleArithmeticException(final ArithmeticException ae, final String operation) throws UndefinedOperationException {
        final String message = ae.getMessage();
        if (message.equalsIgnoreCase("Overflow")) {
            return Infinity.POSITIVE;
        } else if (message.equalsIgnoreCase("Underflow")) {
            return Infinity.NEGATIVE;
        } else if (message.equalsIgnoreCase("Division by zero")) {
            throw new UndefinedOperationException(
                "'" + operation + "' is undefined (division by zero)."
            );
        } else {
            throw new UndefinedOperationException(
                "Error when computing '" + operation + "' (" + message + ")."
            );
        }
    }
}


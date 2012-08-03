package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;

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

    public Real(final String s) {
        this(new BigDecimal(s, mathContext));
    }

    public Real(final double real) {
        this(new BigDecimal(real, mathContext));
    }

    public Real(final BigDecimal real) {
        mReal = new BigDecimal(real.toString(), mathContext);
    }

    public Real(final BigInteger nominator, final BigInteger denominator) {
        BigDecimal n = new BigDecimal(nominator,   mathContext);
        BigDecimal d = new BigDecimal(denominator, mathContext);
        mReal = n.divide(d, mathContext);
    }

    public Real clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public BigDecimal getBigDecimalValue() {
        return mReal;
    }

    public double doubleValue() throws NumberToLargeException {
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
        if (mReal.compareTo(BigDecimal.ZERO) > 0) {
            return Rational.valueOf(mReal.toBigInteger().add(BigInteger.ONE));
        } else {
            return Rational.valueOf(mReal.toBigInteger());
        }
    }

    public Value difference(final Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof NumberValue) {
            if (subtrahend == Infinity.POSITIVE || subtrahend == Infinity.NEGATIVE) {
                return (Infinity) subtrahend.negation();
            }
            BigDecimal right = null;
            if (subtrahend instanceof Real) {
                right = ((Real) subtrahend).mReal;
            } else {
                Rational s = ((Rational) subtrahend);
                right = s.toReal().mReal;
            }
            return new Real(mReal.subtract(right, mathContext));
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a number."
            );
        }
    }

    public Value differenceFlipped(final Rational minuend) throws SetlException {
        return minuend.toReal().difference(this);
    }

    public Value divideFlipped(final Rational dividend) throws SetlException {
        return dividend.toReal().quotient(this);
    }

    public Rational floor() {
        if (mReal.compareTo(BigDecimal.ZERO) < 0) {
            return Rational.valueOf(mReal.toBigInteger().subtract(BigInteger.ONE));
        } else {
            return Rational.valueOf(mReal.toBigInteger());
        }
    }

    public Value integerDivision(final Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            return this.quotient(divisor).floor();
        } else if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " \\ " + divisor + "' is not a number."
            );
        }
    }

    public Real negation() {
        return new Real(mReal.negate(mathContext));
    }

    protected Real power(final int exponent) {
        return new Real(mReal.pow(exponent, mathContext));
    }

    protected NumberValue power(final double exponent) throws NumberToLargeException, IncompatibleTypeException {
        if (mReal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IncompatibleTypeException(
                "Left-hand-side of '" + this + " ** " + exponent + "' is negative."
            );
        }
        final double a = doubleValue(); // may throw exception

        // a ** exponent = exp(ln(a ** exponent) = exp(exponent * ln(a))
        final double r = Math.exp(exponent * Math.log(a));
        if (r == Double.POSITIVE_INFINITY) {
            return Infinity.POSITIVE;
        } else if (r == Double.NEGATIVE_INFINITY) {
            return Infinity.NEGATIVE;
        }
        return new Real(r);
    }

    public Value product(final Value multiplier) throws IncompatibleTypeException {
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
            return new Real(mReal.multiply(right, mathContext));
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a number."
            );
        }
    }

    public Value quotient(final Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            BigDecimal right = null;
            if (divisor instanceof Real) {
                right = ((Real) divisor).mReal;
            } else if (divisor == Infinity.POSITIVE) {
                return new Real(0.0);
            } else if (divisor == Infinity.NEGATIVE) {
                return new Real(-0.0);
            } else {
                Rational d = (Rational) divisor;
                right = d.toReal().mReal;
            }
            try {
                return new Real(mReal.divide(right, mathContext));
            } catch (ArithmeticException ae) {
                throw new UndefinedOperationException(
                    "'" + this + " / " + divisor + "' is undefined."
                );
            }
        } else if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " / " + divisor + "' is not a number."
            );
        }
    }

    public Rational round() {
        return Rational.valueOf(mReal.setScale(0, mathContext.getRoundingMode()).toBigInteger());
    }

    public Value sum(final Value summand) throws IncompatibleTypeException {
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
            return new Real(mReal.add(right, mathContext));
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
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
}


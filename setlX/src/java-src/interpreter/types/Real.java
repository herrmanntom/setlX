package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;

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

    private BigDecimal mReal;

    public Real(String s) {
        this(new BigDecimal(s, mathContext));
    }

    public Real(double real) {
        this(new BigDecimal(real, mathContext));
    }

    public Real(BigDecimal real) {
        mReal = new BigDecimal(real.toString(), mathContext);
    }

    public Real(BigInteger nominator, BigInteger denominator) {
        BigDecimal n = new BigDecimal(nominator,   mathContext);
        BigDecimal d = new BigDecimal(denominator, mathContext);
        mReal = n.divide(d, mathContext);
    }

    public Real clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public double doubleValue() throws NumberToLargeException {
        if ( mReal.abs().compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0 ||
             (
               mReal.abs().compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) < 0 &&
               mReal.compareTo(BigDecimal.ZERO) != 0
             )
           )
        {
            String msg = "The value of " + mReal + " is too large or to small for " +
                         "this operation.";
            throw new NumberToLargeException(msg);
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
        return new Rational(mReal.toBigInteger());
    }

    public Value toRational() {
        int scale = mReal.scale();
        if (scale >= 0) {
            return new Rational(mReal.unscaledValue(), BigInteger.TEN.pow(scale));
        } else /* (scale < 0) */ { // real is in fact an integer
            return new Rational(mReal.unscaledValue().multiply(BigInteger.TEN.pow(scale * -1)));
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
            return new Rational(mReal.toBigInteger().add(BigInteger.ONE));
        } else {
            return new Rational(mReal.toBigInteger());
        }
    }

    public Value difference(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof NumberValue) {
            if (subtrahend == Infinity.POSITIVE || subtrahend == Infinity.NEGATIVE) {
                return (Infinity) subtrahend.negate();
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
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a number.");
        }
    }

    public Value differenceFlipped(Rational minuend) throws SetlException {
        Real left = minuend.toReal();
        return left.difference(this);
    }

    public Value divide(Value divisor) throws SetlException {
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
                throw new UndefinedOperationException("'" + this + " / " + divisor + "' is undefined.");
            }
        } else if (divisor instanceof Term) {
            return ((Term) divisor).divideFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " / " + divisor + "' is not a number.");
        }
    }

    public Value divideFlipped(Rational dividend) throws SetlException {
        Real left = dividend.toReal();
        return left.divide(this);
    }

    public Rational floor() {
        if (mReal.compareTo(BigDecimal.ZERO) < 0) {
            return new Rational(mReal.toBigInteger().subtract(BigInteger.ONE));
        } else {
            return new Rational(mReal.toBigInteger());
        }
    }

    public Value multiply(Value multiplier) throws IncompatibleTypeException {
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
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " * " + multiplier + "' is not a number.");
        }
    }

    public Real negate() {
        return new Real(mReal.negate(mathContext));
    }

    public Real power(int exponent) {
        return new Real(mReal.pow(exponent, mathContext));
    }

    public Real power(double exponent) throws NumberToLargeException, IncompatibleTypeException {
        if (mReal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IncompatibleTypeException("Left-hand-side of '" + this + " ** " + exponent + "' is negative.");
        }
        double a = doubleValue(); // may throw exception

        // a ** exponent = exp(ln(a ** exponent) = exp(exponent * ln(a))
        return new Real(Math.exp(exponent * Math.log(a)));
    }

    public Value sum(Value summand) throws IncompatibleTypeException {
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
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a number or string.");
        }
    }

    /* string and char operations */

    public String toString() {
        return mReal.toString();
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v) {
        if (v instanceof Real) {
            Real nr = (Real) v;
            return mReal.compareTo(nr.mReal);
        } else if (v instanceof Rational) {
            Rational nr = (Rational) v;
            return mReal.compareTo(nr.toReal().mReal);
        } else if (v instanceof SetlError || v == Om.OM || v == Infinity.NEGATIVE || v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) {
            // only SetlError, Om, -Infinity and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

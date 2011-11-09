package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
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

    /*package*/ BigDecimal mReal;

    public Real(String s) {
        mReal = new BigDecimal(s, mathContext);
    }

    public Real(BigDecimal real) {
        mReal = new BigDecimal(real.toString(), mathContext);
    }

    public Real(BigInteger number) {
        mReal = new BigDecimal(number, mathContext);
    }

    public Real(double real) {
        mReal = new BigDecimal(real, mathContext);
    }

    public Real clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isReal() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    public Real absoluteValue() {
        return new Real(mReal.abs());
    }

    public Value add(Value summand) throws IncompatibleTypeException {
        if (summand instanceof NumberValue) {
            if (summand.absoluteValue() == Infinity.POSITIVE) {
                return (Infinity) summand;
            }
            BigDecimal right = null;
            if (summand instanceof Real) {
                right = ((Real) summand).mReal;
            } else {
                right = new BigDecimal(((SetlInt) summand).getNumber());
            }
            return new Real(mReal.add(right, mathContext));
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a number or string.");
        }
    }

    public Real divide(Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            BigDecimal right = null;
            if (divisor instanceof Real) {
                right = ((Real) divisor).mReal;
            } else if (divisor == Infinity.POSITIVE) {
                return new Real(0.0);
            } else if (divisor == Infinity.NEGATIVE) {
                return new Real(-0.0);
            } else {
                right = new BigDecimal(((SetlInt) divisor).getNumber());
            }
            try {
                return new Real(mReal.divide(right, mathContext));
            } catch (ArithmeticException ae) {
                throw new UndefinedOperationException("'" + this + " / " + divisor + "' is undefined.");
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " / " + divisor + "' is not a number.");
        }
    }

    public Real divideFlipped(SetlInt dividend) throws SetlException {
        Real left = new Real(dividend.getNumber());
        return left.divide(this);
    }

    public NumberValue multiply(Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof NumberValue) {
            if (multiplier.absoluteValue() == Infinity.POSITIVE) {
                return (Infinity) multiplier;
            }
            BigDecimal right = null;
            if (multiplier instanceof Real) {
                right = ((Real) multiplier).mReal;
            } else {
                right = new BigDecimal(((SetlInt) multiplier).getNumber());
            }
            return new Real(mReal.multiply(right, mathContext));
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

    public NumberValue subtract(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof NumberValue) {
            if (subtrahend.absoluteValue() == Infinity.POSITIVE) {
                return (Infinity) subtrahend.negate();
            }
            BigDecimal right = null;
            if (subtrahend instanceof Real) {
                right = ((Real) subtrahend).mReal;
            } else {
                right = new BigDecimal(((SetlInt) subtrahend).getNumber());
            }
            return new Real(mReal.subtract(right, mathContext));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a number.");
        }
    }

    public NumberValue subtractFlipped(SetlInt minuend) throws SetlException {
        Real left = new Real(minuend.getNumber());
        return left.subtract(this);
    }

    /* String and Char operations */

    public String toString() {
        return mReal.toString();
    }

    /* Comparisons */

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // Om < SetlBoolean < -Infinity < SetlInt & Real < +Infinity < SetlString < SetlSet < SetlList < ProcedureDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v) {
        if (v instanceof Real) {
            Real nr = (Real) v;
            return mReal.compareTo(nr.mReal);
        } else if (v instanceof SetlInt) {
            SetlInt nr = (SetlInt) v;
            return mReal.compareTo(new BigDecimal(nr.getNumber()));
        } else if (v == Om.OM || v == SetlBoolean.TRUE || v == SetlBoolean.FALSE || v == Infinity.NEGATIVE) {
            // Om, SetlBoolean and -Infinity are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

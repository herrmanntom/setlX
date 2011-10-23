package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class SetlReal extends NumberValue {

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

    public SetlReal(String s) {
        mReal = new BigDecimal(s, mathContext);
    }

    public SetlReal(BigDecimal real) {
        mReal = new BigDecimal(real.toString(), mathContext);
    }

    public SetlReal(BigInteger number) {
        mReal = new BigDecimal(number, mathContext);
    }

    public SetlReal(double real) {
        mReal = new BigDecimal(real, mathContext);
    }

    public SetlReal clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public SetlReal absoluteValue() {
        return new SetlReal(mReal.abs());
    }

    public Value add(Value summand) throws IncompatibleTypeException {
        if (summand instanceof NumberValue) {
            BigDecimal right = null;
            if (summand instanceof SetlReal) {
                right = ((SetlReal) summand).mReal;
            } else {
                right = new BigDecimal(((SetlInt) summand).getNumber());
            }
            return new SetlReal(mReal.add(right, mathContext));
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " + " + summand + "´ is not a number or string.");
        }
    }

    public SetlReal divide(Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            BigDecimal right = null;
            if (divisor instanceof SetlReal) {
                right = ((SetlReal) divisor).mReal;
            } else {
                right = new BigDecimal(((SetlInt) divisor).getNumber());
            }
            try {
                return new SetlReal(mReal.divide(right, mathContext));
            } catch (ArithmeticException ae) {
                throw new UndefinedOperationException("`" + this + " / " + divisor + "´ is undefined.");
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " / " + divisor + "´ is not a number.");
        }
    }

    public SetlReal divideFlipped(SetlInt dividend) throws IncompatibleTypeException {
        BigDecimal left = new BigDecimal(dividend.getNumber());
        return new SetlReal(left.divide(mReal, mathContext));
    }

    public SetlReal multiply(Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof NumberValue) {
            BigDecimal right = null;
            if (multiplier instanceof SetlReal) {
                right = ((SetlReal) multiplier).mReal;
            } else {
                right = new BigDecimal(((SetlInt) multiplier).getNumber());
            }
            return new SetlReal(mReal.multiply(right, mathContext));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " * " + multiplier + "´ is not a number.");
        }
    }

    public SetlReal negate() {
        return new SetlReal(mReal.negate(mathContext));
    }

    public SetlReal power(int exponent) {
        return new SetlReal(mReal.pow(exponent, mathContext));
    }

    public SetlReal subtract(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof NumberValue) {
            BigDecimal right = null;
            if (subtrahend instanceof SetlReal) {
                right = ((SetlReal) subtrahend).mReal;
            } else {
                right = new BigDecimal(((SetlInt) subtrahend).getNumber());
            }
            return new SetlReal(mReal.subtract(right, mathContext));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " - " + subtrahend + "´ is not a number.");
        }
    }

    public SetlReal subtractFlipped(SetlInt minuend) throws IncompatibleTypeException {
        BigDecimal left = new BigDecimal(minuend.getNumber());
        return new SetlReal(left.subtract(mReal, mathContext));
    }

    public String toString() {
        return mReal.toString();
    }

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlList < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v) {
        if (v instanceof SetlReal) {
            SetlReal nr = (SetlReal) v;
            return mReal.compareTo(nr.mReal);
        } else if (v instanceof SetlInt) {
            SetlInt nr = (SetlInt) v;
            return mReal.compareTo(new BigDecimal(nr.getNumber()));
        } else if (v == SetlOm.OM || v instanceof SetlBoolean) {
            // SetlOm and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SetlInt extends NumberValue {

    private BigInteger mNumber;

    public SetlInt(String s){
        mNumber = new BigInteger(s);
    }

    public SetlInt(int number){
        mNumber = BigInteger.valueOf(number);
    }

    public SetlInt(BigInteger number){
        mNumber = number;
    }

    public SetlInt clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public BigInteger getNumber() {
        return mNumber;
    }

    public int intValue() throws NumberToLargeException {
        if (mNumber.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new NumberToLargeException("`" + mNumber + "´ is to large for this operation.");
        } else if (mNumber.compareTo(BigInteger.ZERO) < 0) {
            throw new NumberToLargeException("`" + mNumber + "´ is negative, which is not allowed for this operation.");
        } else {
            return mNumber.intValue();
        }
    }

    public SetlInt absoluteValue() {
        return new SetlInt(mNumber.abs());
    }

    public Value add(Value summand) throws SetlException {
        if (summand instanceof SetlInt) {
            return new SetlInt(mNumber.add(((SetlInt) summand).mNumber));
        } else if (summand instanceof SetlReal) {
            return summand.add(this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " + " + summand + "´ is not a number or string.");
        }
    }

    public NumberValue divide(Value divisor) throws SetlException {
        if (divisor instanceof SetlInt) {
            try {
                return new SetlInt(mNumber.divide(((SetlInt) divisor).mNumber));
            } catch (ArithmeticException ae) {
                throw new UndefinedOperationException("`" + this + " / " + divisor + "´ is undefined.");
            }
        } else if (divisor instanceof SetlReal) {
            return ((SetlReal) divisor).divideFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " / " + divisor + "´ is not a number.");
        }
    }

    public SetlInt mod(Value modulo) throws IncompatibleTypeException {
        if (modulo instanceof SetlInt) {
            return new SetlInt(mNumber.mod(((SetlInt) modulo).mNumber));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " % " + modulo + "´ is not a number.");
        }
    }

    public Value multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof SetlInt) {
            return new SetlInt(mNumber.multiply(((SetlInt) multiplier).mNumber));
        } else if (multiplier instanceof SetlReal || multiplier instanceof SetlString) {
            return multiplier.multiply(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side  of `" + this + " * " + multiplier + "´ is not a number or string.");
        }
    }

    public SetlInt negate() {
        return new SetlInt(mNumber.negate());
    }

    public SetlInt power(int exponent) {
        return new SetlInt(mNumber.pow(exponent));
    }

    public NumberValue subtract(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlInt) {
            return new SetlInt(mNumber.subtract(((SetlInt) subtrahend).mNumber));
        } else if (subtrahend instanceof SetlReal) {
            return ((SetlReal) subtrahend).subtractFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " - " + subtrahend + "´ is not a number.");
        }
    }

    public SetlString charConvert() throws NumberToLargeException {
        if (mNumber.compareTo(BigInteger.valueOf(255)) <= 0 &&
            mNumber.compareTo(BigInteger.ZERO) >= 0)
        {
            return new SetlString("" + (char) mNumber.intValue());
        } else {
            throw new NumberToLargeException("`" + mNumber + "´ is not usable for ASCII conversation (>255 or negative).");
        }
    }

    public String toString() {
        return mNumber.toString();
    }

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlTuple < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v){
        if (v instanceof SetlInt) {
            SetlInt nr = (SetlInt) v;
            return mNumber.compareTo(nr.mNumber);
        } else if (v instanceof SetlReal) {
            SetlReal nr = (SetlReal) v;
            return (new BigDecimal(mNumber)).compareTo(nr.mReal);
        } else if (v instanceof SetlOm || v instanceof SetlBoolean) {
            // SetlOm and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

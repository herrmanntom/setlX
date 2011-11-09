package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;

public class Infinity extends NumberValue {

    public final static Infinity POSITIVE = new Infinity();
    public final static Infinity NEGATIVE = new Infinity();

    private Infinity() {  }

    public Infinity clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public static Infinity get(boolean positive){
        if (positive) {
            return POSITIVE;
        } else {
            return NEGATIVE;
        }
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isInfinity() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    public Infinity absoluteValue() {
        return POSITIVE;
    }

    public Value add(Value summand) throws SetlException {
        if (summand instanceof NumberValue) {
            if (this == summand.negate()) {
                throw new UndefinedOperationException("'" + this + " + " + summand + "' is undefined.");
            }
            return this;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a number or string.");
        }
    }

    public NumberValue divide(Value divisor) throws SetlException {
        if (divisor == POSITIVE || divisor == NEGATIVE) {
            throw new UndefinedOperationException("'" + this + " / " + divisor + "' is undefined.");
        } else if (divisor instanceof NumberValue) {
            if (divisor.compareTo(new SetlInt(0)) < 0) {
                return this.negate();
            } else if (divisor.compareTo(new SetlInt(0)) == 0) {
                throw new UndefinedOperationException("'" + this + " / 0' is undefined.");
            } else {
                return this;
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " / " + divisor + "' is not a number.");
        }
    }

    public NumberValue multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof NumberValue) {
            if (this == multiplier) {
                return POSITIVE;
            } else if (this == multiplier.negate()) {
                return NEGATIVE;
            } else if (multiplier.compareTo(new SetlInt(0)) < 0) {
                return this.negate();
            } else if (multiplier.compareTo(new SetlInt(0)) == 0) {
                throw new UndefinedOperationException("'" + this + " * 0' is undefined.");
            } else {
                return this;
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " * " + multiplier + "' is not a number.");
        }
    }

    public Infinity negate() {
        if (this == POSITIVE) {
            return NEGATIVE;
        } else { // this == NEGATIVE
            return POSITIVE;
        }
    }

    public NumberValue subtract(Value subtrahend) throws SetlException {
        if (subtrahend instanceof NumberValue) {
            if (this == subtrahend) {
                throw new UndefinedOperationException("'" + this + " + " + subtrahend + "' is undefined.");
            }
            return this;
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a number.");
        }
    }

    public Infinity power(int exponent) throws UndefinedOperationException{
        throw new UndefinedOperationException("'" + this + " ** " + exponent + "' is undefined.");
    }

    /* String and Char operations */

    public String toString() {
        if (this == POSITIVE) {
            return "oo";
        } else { // this == NEGATIVE
            return "-oo";
        }
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
        if (this == v) {
            return 0;
        } else if (this == POSITIVE && v instanceof NumberValue) {
            return 1;
        } else if (this == NEGATIVE && v instanceof NumberValue) {
            return -1;
        } else if (v == Om.OM || v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) {
            // Om and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

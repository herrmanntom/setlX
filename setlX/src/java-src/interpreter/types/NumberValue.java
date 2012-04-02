package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;

import java.math.BigInteger;

public abstract class NumberValue extends Value {

    /* arithmetic operations */

    public abstract NumberValue absoluteValue();

    public abstract Value       difference(Value subtrahend) throws SetlException;

    public abstract Value       divide(Value divisor) throws SetlException;

    public abstract Value       multiply(Value multiplier) throws SetlException;

    public abstract NumberValue negate() throws IncompatibleTypeException;

    public          Value       power(Value exponent) throws SetlException {
        if (exponent.isInteger() == SetlBoolean.TRUE) {
            return this.power(((Rational) exponent).intValue());
        } else if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(this);
        } else {
            throw new IncompatibleTypeException("Exponent of '" + this + " ** " + exponent + "' is not an integer.");
        }
    }

    public abstract NumberValue power(int exponent) throws SetlException;

    public abstract Value       sum(Value summand) throws SetlException;

    /* comparisons */

    public final    SetlBoolean isLessThan(Value other) throws IncompatibleTypeException {
        if (other instanceof NumberValue) {
            return SetlBoolean.get(this.compareTo(other) < 0);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " < " + other + "' is not a number.");
        }
    }
}


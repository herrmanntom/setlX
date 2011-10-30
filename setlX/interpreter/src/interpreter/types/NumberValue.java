package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;

import java.math.BigInteger;

public abstract class NumberValue extends Value {

    /* arithmetic operations */

    public abstract NumberValue absoluteValue();

    public abstract Value add(Value summand) throws SetlException;

    public abstract NumberValue divide(Value divisor) throws SetlException;

    public abstract Value multiply(Value multiplier) throws SetlException;

    public abstract NumberValue negate() throws IncompatibleTypeException;

    public NumberValue power(Value exponent) throws SetlException {
        if (exponent instanceof SetlInt) {
            return this.power(((SetlInt) exponent).intValue());
        } else {
            throw new IncompatibleTypeException("Exponent of '" + this + " ** " + exponent + "' is not an integer.");
        }
    }

    public abstract NumberValue power(int exponent);

    public abstract NumberValue subtract(Value subtrahend) throws IncompatibleTypeException;

    /* Comparisons */

    public final SetlBoolean isLessThan(Value other) throws IncompatibleTypeException {
        if (other instanceof NumberValue) {
            return SetlBoolean.get(this.compareTo(other) < 0);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " < " + other + "' is not a number.");
        }
    }
}


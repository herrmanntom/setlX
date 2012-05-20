package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;

public abstract class NumberValue extends Value {

    /* arithmetic operations */

    public abstract NumberValue absoluteValue();

    public abstract Value       difference(Value subtrahend) throws SetlException;

    public abstract Value       divide(Value divisor) throws SetlException;

    public abstract Value       multiply(Value multiplier) throws SetlException;

    public abstract NumberValue negate() throws IncompatibleTypeException;

    public          Value       power(Value exponent) throws SetlException {
        if (exponent.isInteger() == SetlBoolean.TRUE && ((Rational) exponent).intConvertable()) {
            return this.power(((Rational) exponent).intValue());
        } else if (exponent.isRational() == SetlBoolean.TRUE) {
            return this.power(((Real) exponent.toReal()).doubleValue());
        } else if (exponent.isReal() == SetlBoolean.TRUE) {
            Rational r = (Rational) exponent.toRational();
            if (r.isInteger() == SetlBoolean.TRUE && r.intConvertable()) {
                return this.power(r.intValue());
            } else {
                return this.power(((Real) exponent).doubleValue());
            }
        } else if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Exponent of '" + this + " ** " + exponent + "' is not a number."
            );
        }
    }

    public abstract NumberValue power(int    exponent) throws SetlException;
    public abstract NumberValue power(double exponent) throws SetlException;

    public abstract Value       sum(Value summand) throws SetlException;

    /* comparisons */

    public final    SetlBoolean isLessThan(Value other) throws IncompatibleTypeException {
        if (other instanceof NumberValue) {
            return SetlBoolean.get(this.compareTo(other) < 0);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " < " + other + "' is not a number."
            );
        }
    }
}


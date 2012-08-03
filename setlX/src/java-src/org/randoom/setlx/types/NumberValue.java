package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;

public abstract class NumberValue extends Value {

    /* arithmetic operations */

    public abstract NumberValue absoluteValue();
    
    public abstract NumberValue ceil();

    public abstract Value       difference(final Value subtrahend) throws SetlException;

    public abstract NumberValue floor();

    public abstract NumberValue negation() throws IncompatibleTypeException;

    public          Value       power(final Value exponent) throws SetlException {
        if (exponent.isInteger() == SetlBoolean.TRUE && ((Rational) exponent).intConvertable()) {
            return this.power(((Rational) exponent).intValue());
        } else if (exponent.isRational() == SetlBoolean.TRUE) {
            return this.power(((Real) exponent.toReal()).doubleValue());
        } else if (exponent.isReal() == SetlBoolean.TRUE) {
            final Rational r = (Rational) exponent.toRational();
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

    protected abstract NumberValue power(final int    exponent) throws SetlException;
    protected abstract NumberValue power(final double exponent) throws SetlException;

    public    abstract Value       product(final Value multiplier) throws SetlException;

    public    abstract Value       quotient(final Value divisor) throws SetlException;

    public    abstract NumberValue round() throws SetlException;

    public    abstract Value       sum(final Value summand) throws SetlException;

    /* comparisons */

    public final    SetlBoolean isLessThan(final Value other) throws IncompatibleTypeException {
        if (this == other) {
            return SetlBoolean.FALSE;
        } else if (other instanceof NumberValue) {
            return SetlBoolean.valueOf(this.compareTo(other) < 0);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " < " + other + "' is not a number."
            );
        }
    }
}


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class represents a floating point number.
 */
public class SetlDouble extends NumberValue {
    private final double mDouble;

    private SetlDouble(Double d) {
        mDouble = d;
    }
    private SetlDouble(final String s) {
        mDouble = new Double(s);
    }
    private SetlDouble(final BigDecimal real) {
        mDouble = real.doubleValue();
    }
    private SetlDouble(BigInteger nominator, BigInteger denominator) {
        double n = nominator  .doubleValue();
        double d = denominator.doubleValue();
	// not the most efficient way to do it
	while (Double.isInfinite(n) || Double.isInfinite(d)) {
	    nominator   = nominator  .shiftRight(1);
	    denominator = denominator.shiftRight(1);
	    n = nominator  .doubleValue();
	    d = denominator.doubleValue();
	}
	mDouble = n / d;
    }

    public static SetlDouble valueOf(final String str) {
        return new SetlDouble(str);
    }
    public static NumberValue valueOf(final double real) {
	return new SetlDouble(real);
    }
    public static NumberValue valueOf(final BigDecimal real) throws UndefinedOperationException {
        Double value = real.doubleValue();
	if (Double.isInfinite(value)) {
            if (value > 0) {
                return Infinity.POSITIVE;
            } else {
                return Infinity.NEGATIVE;
            }
        } else if (Double.isNaN(value)) {
            throw new UndefinedOperationException(
                "Result of this operation is undefined/not a number."
            );
        }
	return new SetlDouble(value);
    }
    public static SetlDouble valueOf(final BigInteger nominator, final BigInteger denominator) {
        return new SetlDouble(nominator, denominator);
    }

    @Override
    public SetlDouble clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }
    public Double getDoubleValue() {
        return mDouble;
    }

    /* type checks (sort of Boolean operation) */
    @Override
    public SetlBoolean isDouble() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */
    @Override
    public Rational toInteger(final State state) 
	throws UndefinedOperationException
    {
	if (mDouble >= 0.0) {
	    SetlDouble result = new SetlDouble(Math.floor(mDouble));
	    return result.toRational(state);
	} else {
	    SetlDouble result = new SetlDouble(Math.ceil(mDouble));
	    return result.toRational(state);
	}
    }

    /* Convert an IEEE 754 double to a rational.  The IEEE format specifies
       that bit number 63 is the sign bit, the bits in position 62 to 52
       represent a biased exponent.  The bias is 1023.  The bits from position
       51 down to position 0 are the mantissa.  Note that, as long as the numbers
       are normalized, the mantissa is extended with a 1 bit at position 53.
       A number is denormnalized if the biased exponent has the value 0.  In that 
       case, the bias only has the value 1022.
     */
    @Override
    public Rational toRational(final State state) 
	throws UndefinedOperationException 
    {
	long bits         = Double.doubleToLongBits(mDouble);
	long signMask     = 0x8000000000000000L;
	long exponentMask = 0x7ff0000000000000L;
        long valueMask    = 0x000fffffffffffffL;
	long biasedExp    = ((bits & exponentMask) >>> 52);
	boolean sign      = ((bits & signMask) == signMask);
	BigInteger nominator   = null;
	BigInteger denominator = null;
	if (biasedExp == 0) {  // denormalized number
	    long exponent = - 1022 - 52;
	    long mantissa = bits & valueMask;	    
	    if (sign) {
		nominator = BigInteger.valueOf(-mantissa);
	    } else {
		nominator = BigInteger.valueOf(mantissa);
	    }
	    denominator = BigInteger.valueOf(1).shiftLeft((int) -exponent);
	    return Rational.valueOf(nominator, denominator);
	} else if (biasedExp < 2047) {  // normalized number
	    long exponent = biasedExp - 1023 - 52;
	    long mantissa = (1L << 52) | (bits & valueMask);
	    if (sign) {
		nominator = BigInteger.valueOf(-mantissa);
	    } else {
		nominator = BigInteger.valueOf(mantissa);
	    }
	    if (exponent < 0) {
		denominator = BigInteger.valueOf(1).shiftLeft((int) -exponent);
		return Rational.valueOf(nominator, denominator);
	    } else {
		nominator = nominator.shiftLeft((int) exponent);
		return Rational.valueOf(nominator);
	    }
	} else { // not a number (NaN)
	    String msg = "This is not a number (NaN).";
	    throw new UndefinedOperationException(msg);	    
	}
    }

    @Override
    public NumberValue toReal(final State state) throws UndefinedOperationException {
	return Real.valueOf(mDouble);
    }

    @Override
    public SetlDouble toDouble(final State state) {
        return this;
    }

    /* native type checks */
    @Override
    public boolean jDoubleConvertable() {
	return true;
    }

    /* native type conversions */
    @Override
    public double jDoubleValue() {
	return mDouble;
    }
    @Override
    public double toJDoubleValue(final State state) {
	return mDouble;
    }

    /* arithmetic operations */
    @Override
    public SetlDouble absoluteValue(final State state) {
        return new SetlDouble(Math.abs(mDouble));
    }

    @Override
    public Rational ceil(final State state) throws UndefinedOperationException {
	// TODO: this can be done faster
	SetlDouble result = new SetlDouble(Math.ceil(mDouble));
	return result.toRational(state);
    }

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof SetlDouble) {
	    SetlDouble rhs = (SetlDouble) subtrahend;
	    return new SetlDouble(mDouble - rhs.getDoubleValue());
	}
        if (subtrahend instanceof NumberValue) {
            if (subtrahend instanceof Infinity) {
		Infinity rhs = (Infinity) subtrahend;
		return new SetlDouble(mDouble - rhs.jDoubleValue());
	    }
	    if (subtrahend instanceof Real) {
		Real   rhs       = (Real) subtrahend;
		double rhsDouble = rhs.getBigDecimalValue().doubleValue();
		return new SetlDouble(mDouble - rhsDouble);
            } else { // subtrahend must be Rational now
                Rational rhs = (Rational) subtrahend;
		return new SetlDouble(mDouble - rhs.toDouble().mDouble);
            }
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a number."
            );
        }
    }

    public Value differenceFlipped(final State state, final Rational minuend) throws SetlException {
	BigInteger n   = minuend.getNominatorValue();
	BigInteger d   = minuend.getDenominatorValue();
	SetlDouble lhs = SetlDouble.valueOf(n, d);
        return lhs.difference(state, this);
    }
    public Value differenceFlipped(final State state, final Real minuend) throws SetlException {
        return minuend.toDouble(state).difference(state, this);
    }

    @Override
    public Rational floor(final State state) 
	throws UndefinedOperationException 
    {
	// TODO: this can be done faster
	SetlDouble result = new SetlDouble(Math.floor(mDouble));
	return result.toRational(state);
    }

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof SetlDouble) {
	    SetlDouble rhs = (SetlDouble) divisor;
	    return new SetlDouble(mDouble / rhs.getDoubleValue()).floor(state);
	}
	if (divisor instanceof Real) {
	    final Real rhs = (Real) divisor;
	    return new SetlDouble(mDouble / rhs.jDoubleValue()).floor(state);
	}
	if (divisor instanceof Rational) {
	    final Rational rhs = (Rational) divisor;
	    return new SetlDouble(mDouble / rhs.toDouble().mDouble).floor(state);
	}
	if (divisor instanceof Infinity) {
	    final Infinity rhs = (Infinity) divisor;
	    // no need for floor here as result is either 0 or NaN
	    return new SetlDouble(mDouble / rhs.jDoubleValue());
	}
        if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " \\ " + divisor + "' is not a number."
            );
        }
    }

    @Override
    public NumberValue minus(final State state) throws UndefinedOperationException {
	return new SetlDouble(-mDouble);
    }

    @Override
    protected SetlDouble power(final State state, final int exponent) {
	return new SetlDouble(Math.pow(mDouble, exponent));
    }

    @Override
    protected NumberValue power(final State state, final double exponent) {
	return new SetlDouble(Math.pow(mDouble, exponent));
    }

    @Override
    public Value product(final State state, final Value multiplier) throws IncompatibleTypeException
    {
	if (multiplier instanceof SetlDouble) {
	    SetlDouble rhs = (SetlDouble) multiplier;
	    return new SetlDouble(mDouble * rhs.getDoubleValue());
	}
	if (multiplier instanceof NumberValue) {
            if (multiplier instanceof Infinity) {
		Double d = mDouble;
		if (d.equals(0.0)) {
		    return new SetlDouble(Double.NaN);
		} else {
		    return (Infinity) multiplier;
		}
            }
            if (multiplier instanceof Real) {
		Real   rhs       = (Real) multiplier;
		double rhsDouble = rhs.getBigDecimalValue().doubleValue();
		return new SetlDouble(mDouble * rhsDouble);
            } else {  // multiplier must be Rational now
                final Rational rhs = (Rational) multiplier;
                return new SetlDouble(mDouble * rhs.toDouble().mDouble);
            }
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a number."
            );
        }
    }

    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
	if (divisor instanceof SetlDouble) {
	    SetlDouble rhs = (SetlDouble) divisor;
	    return new SetlDouble(mDouble / rhs.getDoubleValue());
	}
        if (divisor instanceof NumberValue) {
            if (divisor instanceof Infinity) {
		Infinity rhs = (Infinity) divisor;
     		return new SetlDouble(mDouble / rhs.jDoubleValue());
            }
	    if (divisor instanceof Real) {
		Real   rhs       = (Real) divisor;
		double rhsDouble = rhs.getBigDecimalValue().doubleValue();
		return new SetlDouble(mDouble / rhsDouble);
            } else {  // divisor must be Rational now
		Rational rhs = (Rational) divisor;
                return new SetlDouble(mDouble / rhs.toDouble().mDouble);
            }
        } else if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " / " + divisor + "' is not a number."
            );
        }
    }
    public Value quotientFlipped(final State state, final Rational dividend) throws SetlException {
	BigInteger n   = dividend.getNominatorValue();
	BigInteger d   = dividend.getDenominatorValue();
	SetlDouble lhs = SetlDouble.valueOf(n, d);
        return lhs.quotient(state, this);
    }
    public Value quotientFlipped(final State state, final Real dividend) throws SetlException {
        return dividend.toDouble(state).quotient(state, this);
    }

    @Override
    public Rational round(final State state) throws UndefinedOperationException {
	SetlDouble result = new SetlDouble(Math.floor(mDouble + 0.5));
        return result.toRational(state);
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof SetlDouble) {
	    SetlDouble rhs = (SetlDouble) summand;
	    return new SetlDouble(mDouble + rhs.getDoubleValue());
	}
        if (summand instanceof NumberValue) {
            if (summand instanceof Infinity) {
		Infinity rhs = (Infinity) summand;
		return new SetlDouble(mDouble + rhs.jDoubleValue());
	    }
	    if (summand instanceof Real) {
		Real   rhs       = (Real) summand;
		double rhsDouble = rhs.getBigDecimalValue().doubleValue();
		return new SetlDouble(mDouble + rhsDouble);
            } else { // summand must be Rational now
                Rational rhs = (Rational) summand;
		return new SetlDouble(mDouble + rhs.toDouble().mDouble);
            }
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a number or string."
            );
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
	Double d = mDouble;
	sb.append(d.toString());
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlDouble) {
	    final SetlDouble rhs = (SetlDouble) v;
	    final Double d = mDouble;
	    return d.compareTo(rhs.mDouble);
        } else if (v instanceof Real) {
            final Real rhs = (Real) v;
	    final Double d = mDouble;
            return d.compareTo(rhs.getBigDecimalValue().doubleValue());
        } else if (v instanceof Rational) {
            final Rational rhs = (Rational) v;
	    final Double d = mDouble;
            return d.compareTo(rhs.toDouble().mDouble);
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    /* To compare "incomparable" values, e.g. of different types, the following
     * order is established and used in compareTo():
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    protected int compareToOrdering() {
        return 500;
    }

    @Override
    public boolean equalTo(final Value v) {
	return this.compareTo(v) == 0;
    }

    private final static int initHashCode = SetlDouble.class.hashCode();

    @Override
    public int hashCode() {
	Double d = mDouble;
        return initHashCode + d.hashCode();
    }
}


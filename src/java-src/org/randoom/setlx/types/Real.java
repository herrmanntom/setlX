package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Real extends NumberValue {

    public final static int PRINT_MODE_DEFAULT     = 1;
    public final static int PRINT_MODE_ENGINEERING = 2;
    public final static int PRINT_MODE_PLAIN       = 3;

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

    private final BigDecimal real;

    private Real(final String s) {
        this(new BigDecimal(s, mathContext));
    }

    private Real(final BigDecimal real) {
        this.real = new BigDecimal(real.toString(), mathContext);
    }

    private Real(final BigInteger nominator, final BigInteger denominator) {
        final BigDecimal n = new BigDecimal(nominator,   mathContext);
        final BigDecimal d = new BigDecimal(denominator, mathContext);
        this.real = n.divide(d, mathContext);
    }

    public static Real valueOf(final String str) {
        return new Real(str);
    }

    public static NumberValue valueOf(final double real) throws UndefinedOperationException {
        if (Double.isInfinite(real)) {
            if (real > 0) {
                return Infinity.POSITIVE;
            } else {
                return Infinity.NEGATIVE;
            }
        } else if (Double.isNaN(real)) {
            throw new UndefinedOperationException(
                "Result of this operation is undefined/not a number."
            );
        }
        return new Real(Double.toString(real));
    }

    public static Real valueOf(final BigDecimal real) {
        return new Real(real);
    }

    public static Real valueOf(final BigInteger nominator, final BigInteger denominator) {
        return new Real(nominator, denominator);
    }

    @Override
    public Real clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    public BigDecimal getBigDecimalValue() {
        return real;
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isReal() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    @Override
    public Rational toInteger(final State state) {
        return Rational.valueOf(real.toBigInteger());
    }

    @Override
    public Rational toRational(final State state) {
        final int scale = real.scale();
        if (scale >= 0) {
            return Rational.valueOf(real.unscaledValue(), BigInteger.TEN.pow(scale));
        } else /* (scale < 0) */ { // real is in fact an integer
            return Rational.valueOf(real.unscaledValue().multiply(BigInteger.TEN.pow(scale * -1)));
        }
    }

    @Override
    public Real toReal(final State state) {
        return this;
    }

    /* native type checks */

    @Override
    public boolean jDoubleConvertable() {
        return ( real.abs().compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0 &&
                    (
                        real.abs().compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) > 0 ||
                        real.compareTo(BigDecimal.ZERO) == 0
                    )
               );
    }

    /* native type conversions */

    @Override
    public double jDoubleValue() throws NumberToLargeException {
        if ( real.abs().compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0 ||
             (
               real.abs().compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) < 0 &&
               real.compareTo(BigDecimal.ZERO) != 0
             )
           )
        {
            throw new NumberToLargeException(
                "The value of " + real + " is too large or to small for this operation."
            );
        } else {
            return real.doubleValue();
        }
    }

    /* arithmetic operations */

    @Override
    public Real absoluteValue(final State state) {
        return new Real(real.abs());
    }

    @Override
    public Rational ceil(final State state) {
        final BigInteger intValue = real.toBigInteger();
        if (real.compareTo(new BigDecimal(intValue)) == 0 || real.compareTo(BigDecimal.ZERO) < 0) {
            return Rational.valueOf(intValue);
        } else /* if (mReal.compareTo(BigDecimal.ZERO) > 0) */ {
            return Rational.valueOf(intValue.add(BigInteger.ONE));
        }
    }

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof NumberValue) {
            if (subtrahend == Infinity.POSITIVE || subtrahend == Infinity.NEGATIVE) {
                return (Infinity) subtrahend.minus(state);
            }
            BigDecimal right = null;
            if (subtrahend instanceof Real) {
                right = ((Real) subtrahend).real;
            } else {
                final Rational s = ((Rational) subtrahend);
                right = s.toReal(state).real;
            }
            try {
                return Real.valueOf(real.subtract(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " - " + subtrahend);
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
        return minuend.toReal(state).difference(state, this);
    }

    @Override
    public Rational floor(final State state) {
        final BigInteger intValue = real.toBigInteger();
        if (real.compareTo(new BigDecimal(intValue)) == 0 || real.compareTo(BigDecimal.ZERO) > 0) {
            return Rational.valueOf(intValue);
        } else /* if (mReal.compareTo(BigDecimal.ZERO) < 0) */ {
            return Rational.valueOf(intValue.subtract(BigInteger.ONE));
        }
    }

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof NumberValue) {
            return this.quotient(state, divisor).floor(state);
        } else if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " \\ " + divisor + "' is not a number."
            );
        }
    }

    @Override
    public NumberValue minus(final State state) throws UndefinedOperationException {
        try {
            return new Real(real.negate(mathContext));
        } catch (final ArithmeticException ae) {
            return handleArithmeticException(ae, "-" + this);
        }
    }

    @Override
    protected NumberValue power(final State state, final int exponent) throws UndefinedOperationException {
        try {
            return Real.valueOf(real.pow(exponent, mathContext));
        } catch (final ArithmeticException ae) {
            return handleArithmeticException(ae, this + " ** " + exponent);
        }
    }

    @Override
    protected NumberValue power(final State state, final double exponent) throws NumberToLargeException, IncompatibleTypeException, UndefinedOperationException {
        if (real.compareTo(BigDecimal.ZERO) < 0) {
            throw new IncompatibleTypeException(
                "Left-hand-side of '" + this + " ** " + exponent + "' is negative."
            );
        }
        final double a = jDoubleValue(); // may throw exception

        // a ** exponent = exp(ln(a ** exponent) = exp(exponent * ln(a))
        return Real.valueOf(Math.exp(exponent * Math.log(a)));
    }

    @Override
    public Value product(final State state, final Value multiplier) throws IncompatibleTypeException, UndefinedOperationException {
        if (multiplier instanceof NumberValue) {
            if (multiplier == Infinity.POSITIVE || multiplier == Infinity.NEGATIVE) {
                return (Infinity) multiplier;
            }
            BigDecimal right = null;
            if (multiplier instanceof Real) {
                right = ((Real) multiplier).real;
            } else {
                final Rational m = (Rational) multiplier;
                right = m.toReal(state).real;
            }
            try {
                return Real.valueOf(real.multiply(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " * " + multiplier);
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
        if (divisor instanceof NumberValue) {
            BigDecimal right = null;
            if (divisor == Infinity.POSITIVE) {
                return new Real("0.0");
            } else if (divisor == Infinity.NEGATIVE) {
                return new Real("-0.0");
            } else if (divisor instanceof Real) {
                right = ((Real) divisor).real;
            } else {
                right = ((Rational) divisor).toReal(state).real;
            }
            if (right.compareTo(BigDecimal.ZERO) == 0) {
                final int cmp = this.compareTo(Rational.ZERO);
                if (cmp > 0) {
                    return Infinity.POSITIVE;
                } else if (cmp < 0) {
                    return Infinity.NEGATIVE;
                } else {
                    throw new UndefinedOperationException(
                        "'" + this + " / " + divisor + "' is undefined."
                    );
                }
            }
            try {
                return Real.valueOf(real.divide(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " / " + divisor);
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
        return dividend.toReal(state).quotient(state, this);
    }

    @Override
    public Rational round(final State state) {
        return Rational.valueOf(real.setScale(0, mathContext.getRoundingMode()).toBigInteger());
    }

    @Override
    public Value sum(final State state, final Value summand) throws IncompatibleTypeException, UndefinedOperationException {
        if (summand instanceof NumberValue) {
            if (summand == Infinity.POSITIVE || summand == Infinity.NEGATIVE) {
                return (Infinity) summand;
            }
            BigDecimal right = null;
            if (summand instanceof Real) {
                right = ((Real) summand).real;
            } else {
                final Rational s = (Rational) summand;
                right = s.toReal(state).real;
            }
            try {
                return Real.valueOf(real.add(right, mathContext));
            } catch (final ArithmeticException ae) {
                return handleArithmeticException(ae, this + " + " + summand);
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
        switch(state.realPrintMode) {
            case PRINT_MODE_DEFAULT:
                sb.append(real.toString());
                break;
            case PRINT_MODE_ENGINEERING:
                sb.append(real.toEngineeringString());
                break;
            case PRINT_MODE_PLAIN:
                sb.append(real.toPlainString());
                break;
        }
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
        } else if (v instanceof Real) {
            final Real nr = (Real) v;
            return real.compareTo(nr.real);
        } else if (v instanceof Rational) {
            final Rational nr = (Rational) v;
            return real.compareTo(nr.toReal().real);
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
        if (this == v) {
            return true;
        } else if (v instanceof Real) {
            return real.compareTo(((Real) v).real) == 0;
        } else if (v instanceof Rational) {
            return real.compareTo(((Rational) v).toReal().real) == 0;
        } else {
            return false;
        }
    }

    private final static int initHashCode = Real.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode + real.hashCode();
    }

    /* private */

    public Infinity handleArithmeticException(final ArithmeticException ae, final String operation) throws UndefinedOperationException {
        final String message = ae.getMessage();
        if (message.equalsIgnoreCase("Overflow")) {
            return Infinity.POSITIVE;
        } else if (message.equalsIgnoreCase("Underflow")) {
            return Infinity.NEGATIVE;
        } else if (message.equalsIgnoreCase("Division by zero")) {
            throw new UndefinedOperationException(
                "'" + operation + "' is undefined (division by zero)."
            );
        } else {
            throw new UndefinedOperationException(
                "Error when computing '" + operation + "' (" + message + ")."
            );
        }
    }
}


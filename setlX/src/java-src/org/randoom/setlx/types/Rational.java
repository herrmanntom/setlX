package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.Environment;

import java.math.BigInteger;

// This class represents a rational number
public class Rational extends NumberValue {

    private final           BigInteger  mNominator;
    private final           BigInteger  mDenominator;
    private final           boolean     mIsInteger;

    public  final static    Rational    ZERO            = new Rational(0);
    public  final static    Rational    ONE             = new Rational(1);
    public  final static    Rational    TWO             = new Rational(2);
    public  final static    Rational    THREE           = new Rational(3);
    public  final static    Rational    FOUR            = new Rational(4);
    public  final static    Rational    FIVE            = new Rational(5);
    public  final static    Rational    SIX             = new Rational(6);
    public  final static    Rational    SEVEN           = new Rational(7); // of nine?
    public  final static    Rational    EIGHT           = new Rational(8);
    public  final static    Rational    NINE            = new Rational(9);
    public  final static    Rational    TEN             = new Rational(10);

    private final static    Rational[]  NUMBERS         = {ZERO, ONE, TWO, THREE,
                                                           FOUR, FIVE, SIX, SEVEN,
                                                           EIGHT, NINE, TEN};

    private Rational(final long number) {
        this(BigInteger.valueOf(number));
    }

    private Rational(final BigInteger number) {
        mNominator      = number;
        mDenominator    = BigInteger.ONE;
        mIsInteger      = true;
    }

    // This constructor creates a new rational number with nominator n and
    // denominator d.  Care is taken, that the denominator is always positive
    // and that the rational number is in lowest terms.
    private Rational(BigInteger n, BigInteger d) {
        if (d.signum() == -1) {
            n = n.negate();
            d = d.negate();
        }
        final BigInteger ggt = n.gcd(d);
        mNominator           = n.divide(ggt);
        mDenominator         = d.divide(ggt);
        mIsInteger           = mDenominator.compareTo(BigInteger.ONE) == 0;
        if (mDenominator.compareTo(BigInteger.ZERO) == 0) {
            throw new NumberFormatException("new Rational: Devision by zero!");
        }
    }

    public static Rational valueOf(final long number) {
        if (number >= 0 && number <= 10) {
            return NUMBERS[(int) number];
        }
        return new Rational(number);
    }

    public static Rational valueOf(final String string) {
        final int pos = string.indexOf('/');
        if (pos == -1) { // does not include '/'
            try {
                return valueOf(Long.parseLong(string));
            } catch (final NumberFormatException nfe) {
                return new Rational(new BigInteger(string));
            }
        } else {
            return new Rational(
                new BigInteger(string.substring(0, pos)),
                new BigInteger(string.substring(pos + 1))
            );
        }
    }

    public static Rational valueOf(final BigInteger number) {
        return new Rational(number);
    }

    public static Rational valueOf(final BigInteger nominator, final BigInteger denominator) {
        return new Rational(nominator, denominator);
    }

    public Rational clone() {
        // this value is immutable and can not be changed once set
        return this;
    }

    public BigInteger getNominatorValue() {
        return mNominator;
    }

    public BigInteger getDenominatorValue() {
        return mDenominator;
    }

    public boolean intConvertable() {
        return (mIsInteger &&
                mNominator.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0 &&
                mNominator.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) > 0
               );
    }

    public int intValue() throws NotAnIntegerException, NumberToLargeException {
        if (! mIsInteger) {
            throw new NotAnIntegerException(
                "The fraction " + mNominator + "/" + mDenominator + " can't be converted" +
                " to an integer as the denominator is not 1."
            );
        }
        if (mNominator.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ||
            mNominator.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0)
        {
            throw new NumberToLargeException(
                "The absolute value of " + mNominator + " is too large or to small for " +
                "this operation."
            );
        } else {
            return mNominator.intValue();
        }
    }

    public boolean isPrime() {
        final BigInteger two = BigInteger.valueOf(2);
        if ( ! mIsInteger || mNominator.compareTo(BigInteger.ONE) <= 0) {
            return false;
        } else if (mNominator.compareTo(two) == 0) {
            return true;
        } else if (mNominator.mod(two).compareTo(BigInteger.ZERO) == 0) {
            return false;
        }

        BigInteger i = BigInteger.valueOf(3);
        while ( i.multiply(i).compareTo(mNominator) <= 0) {
            if (mNominator.mod(i).compareTo(BigInteger.ZERO) == 0) {
                return false;
            }
            i = i.add(two);
        }

        return true;
    }

    public boolean isProbablePrime() {
        if ( ! mIsInteger ) {
            return false;
        } else if (mNominator.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0) {
            return mNominator.isProbablePrime(15);
        }
        return mNominator.isProbablePrime(30);
    }

    public Rational nextProbablePrime() throws NotAnIntegerException {
        if ( ! mIsInteger || mNominator.compareTo(BigInteger.ZERO) <= 0) {
            throw new NotAnIntegerException(
                "'" + this + "' is not an integer >= 1."
            );
        }
        return new Rational(mNominator.nextProbablePrime());
    }

    /* type check (sort of Boolean operation) */
    public SetlBoolean isInteger() {
        if (mIsInteger) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public SetlBoolean isRational() {
        return SetlBoolean.TRUE;
    }

    /* type conversion */
    public Rational toInteger() {
        if (mIsInteger) {
            return this;
        } else {
            return new Rational(mNominator.divide(mDenominator));
        }
    }

    public Rational toRational() {
        return this;
    }

    public Real toReal() {
        return new Real(mNominator, mDenominator);
    }

    /* arithmetic operations */
    public Rational absoluteValue() {
        return new Rational(mNominator.abs(), mDenominator.abs());
    }

    // The ceil(ing) of a number x is defined as the lowest integer n such that n => x.
    // The calculation is rather complicated because the integer division of negative
    // numbers in Java does not satisfy the mathematical specification that
    // a = (a/b) * b + r with 0 <= r < b.  Rather, Java always rounds to 0.
    public Rational ceil() {
        if (mNominator.compareTo(BigInteger.ZERO) > 0 &&
             ! mIsInteger
           )
        {
            BigInteger q = mNominator.divide(mDenominator).add(BigInteger.ONE);
            return new Rational(q);
        }
        return new Rational(mNominator.divide(mDenominator));
    }

    public Value difference(final Value subtrahend) throws SetlException {
        if (subtrahend instanceof Rational) {
            final Rational s = (Rational) subtrahend;
            if (mIsInteger && s.mIsInteger) {
                // mNominator/1 - s.mNominator/1  <==>  mNominator - s.mNominator
                return new Rational(mNominator.subtract(s.mNominator));
            } else {
                // mNominator/mDenominator - s.mNominator/s.mDenominator = (mNominator * s.mDenominator - mDenominator * s.mNominator) / (mDenominator * s.mDenominator)
                final BigInteger n = mNominator.multiply(s.mDenominator).subtract(mDenominator.multiply(s.mNominator));
                final BigInteger d = mDenominator.multiply(s.mDenominator);
                return new Rational(n, d);
            }
        } else if (subtrahend instanceof Real) {
            return ((Real) subtrahend).differenceFlipped(this);
        } else if (subtrahend == Infinity.POSITIVE ||
                   subtrahend == Infinity.NEGATIVE)
        {
            return (Infinity) subtrahend.negate();
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a number."
            );
        }
    }

    public Value divide(final Value divisor) throws SetlException {
        if (divisor instanceof Rational) {
                final Rational d = (Rational) divisor;
                if (mIsInteger && d.mIsInteger) {
                    // (mNominator/1) / (d.mNominator/1)  <==>  mNominator / d.mNominator
                    if (d.mNominator.compareTo(BigInteger.ZERO) == 0) {
                        throw new UndefinedOperationException("'" + this + " / 0' is undefined.");
                    }
                    return new Rational(mNominator, d.mNominator);
                } else {
                    // (mNominator/mDenominator) / (d.mNominator/d.mDenominator) = (mNominator * d.mDenominator) / (mDenominator * d.mNominator)
                    if (d.mNominator.compareTo(BigInteger.ZERO) == 0) {
                        throw new UndefinedOperationException("'(" + this + ") / 0' is undefined.");
                    }
                    return new Rational(mNominator.multiply(d.mDenominator), mDenominator.multiply(d.mNominator));
                }
        } else if (divisor instanceof Real) {
            return ((Real) divisor).divideFlipped(this);
        } else if (divisor == Infinity.POSITIVE || divisor == Infinity.NEGATIVE) {
            return Rational.ZERO;
        } else if (divisor instanceof Term) {
            return ((Term) divisor).divideFlipped(this);
        } else {
            String msg = "Right-hand-side of '(" + this + ") / (" + divisor +
                         ")' is not a number.";
            throw new IncompatibleTypeException(msg);
        }
    }

    // subclass for threaded factorial computation
    class Factorial extends Thread {
        private final int        from;
        private final int        to;
        /*package*/   BigInteger result;

        public Factorial(final int from, final int to) {
            this.from   = from;
            this.to     = to;
            this.result = BigInteger.valueOf(from);
        }

        public void run() {
            for (int i = from + 1; i < to; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        }
    }

    public Rational factorial() throws SetlException {
        if (mNominator.compareTo(BigInteger.ZERO) < 0 ||
            mDenominator.compareTo(BigInteger.ONE) != 0)
        {
            throw new UndefinedOperationException(
                "'(" + this + ")!' is undefined."
            );
        }
        // The next line will throw an exception if mNominator > 2^31,
        // but wanting that is crazy talk
        final int        n      = intValue();
              BigInteger result = BigInteger.ONE;
        final int        CORES  = Environment.getNumberOfCores();
        // use simple implementation when computing small factorials or having
        // only one CPU (less overhead)
        if (n <= 512 || CORES <= 1) {
            for (int i = 2; i <= n; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        } else { // use multiple threads for bigger factorials
            // create as many threads as there are processors
            final Factorial threads[] = new Factorial[CORES];
            for (int i = 0; i < CORES; ++i) {
                final int from = n/CORES * (i) + 1;
                      int to   = n/CORES * (i + 1) + 1;
                if (i == CORES - 1) { // last thread
                    to += n % CORES; // pick up the slack
                }
                threads[i] = new Factorial(from, to);
                // start thread
                threads[i].start();
            }
            // wait for them to complete and compile end result
            for (int i = 0; i < CORES; ++i) {
                try {
                    threads[i].join();
                    result = result.multiply(threads[i].result);
                } catch (InterruptedException ie) {
                    throw new StopExecutionException("Interrupted");
                }
            }
        }
        return new Rational(result);
    }

    public void fillCollectionWithinRange(final Value step_,
                                          final Value stop_,
                                          final CollectionValue collection)
        throws SetlException
    {
        Rational step = null;
        Rational stop = null;

        // check if types make sense (essentially only numbers make sense here)
        if (step_ instanceof Rational) {
            step = (Rational) step_;
        } else {
            throw new IncompatibleTypeException(
                "Step size '" + step_ + "' is not a rational number."
            );
        }
        if (stop_ instanceof Rational) {
            stop = (Rational) stop_;
        } else {
            throw new IncompatibleTypeException(
                "Stop argument '" + stop_ + "' is not a rational number."
            );
        }
        // collect all elements in range
        Rational i = this;
        if (step.compareTo(Rational.ZERO) > 0) {
            for (; i.compareTo(stop) <= 0; i = (Rational) i.sum(step)) {
                collection.addMember(i);
            }
        } else if (step.compareTo(Rational.ZERO) < 0) {
            for (; i.compareTo(stop) >= 0; i = (Rational) i.sum(step)) {
                collection.addMember(i);
            }
        } else { // step == 0!
            throw new UndefinedOperationException(
                "Step size '" + step + "' is illogical."
            );
        }
    }

    // The floor of a number x is defined as the biggest integer n such that n <= x.
    // The calculation is rather complicated because the integer division of negative
    // numbers in Java does not satisfy the mathematical specification that
    // a = (a/b) * b + r with 0 <= r < b.  Rather, Java always rounds to 0.
    public Rational floor() {
        if (mNominator.compareTo(BigInteger.ZERO) < 0 &&
             ! mIsInteger
           )
        {
            final BigInteger q = mNominator.divide(mDenominator).subtract(BigInteger.ONE);
            return new Rational(q);
        }
        return new Rational(mNominator.divide(mDenominator));
    }

    // The mathematical specification of the modulo function is:
    //     a % b = a - floor(a/b) * b
    public Value modulo(final Value modulo) throws IncompatibleTypeException, SetlException {
        if (modulo instanceof Rational) {
            final Rational b = (Rational) modulo;
            if (mIsInteger && b.mIsInteger) {
                return new Rational(mNominator.mod(b.mNominator));
            } else {
                final Rational ab = (Rational) this.divide(b);
                return this.difference(ab.floor().multiply(b));
            }
        } else if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " % " + modulo + "' is not a rational number."
            );
        }
    }

    public Value multiply(final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final Rational r = (Rational) multiplier;
            if (mIsInteger && r.mIsInteger) {
                // mNominator/1 * r.mNominator/1  <==>  mNominator * r.mNominator
                return new Rational(mNominator.multiply(r.mNominator));
            } else {
                // mNominator/mDenominator * r.mNominator/r.mDenominator = (mNominator * r.mNominator) / (mDenominator * r.mDenominator)
                return new Rational(mNominator.multiply(r.mNominator), mDenominator.multiply(r.mDenominator));
            }
        } else if (multiplier instanceof Real       ||
                   multiplier instanceof SetlString ||
                   multiplier == Infinity.POSITIVE  ||
                   multiplier == Infinity.NEGATIVE)
        {
            return multiplier.multiply(this);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a number or string."
            );
        }
    }

    public Rational negate() {
        return new Rational(mNominator.negate(), mDenominator);
    }

    protected Rational power(final int exponent) throws NumberToLargeException {
        if (exponent >= 0) {
            return new Rational(mNominator  .pow(exponent     ), mDenominator.pow(exponent     ));
        } else {
            return new Rational(mDenominator.pow(exponent * -1), mNominator  .pow(exponent * -1));
        }
    }

    protected Real power(final double exponent) throws SetlException {
        return toReal().power(exponent);
    }

    public Rational round() throws SetlException {
        if (mIsInteger) {
            return this;
        } else {
            final Rational roundPart = (Rational) this.difference(this.toInteger()).toReal().round();
            return (Rational) this.toInteger().sum(roundPart);
        }
    }

    public Value sum(final Value summand) throws SetlException {
        if (summand instanceof Rational) {
            final Rational r = (Rational) summand;
            if (mIsInteger && r.mIsInteger) {
                // mNominator/1 + r.mNominator/1  <==>  mNominator + r.mNominator
                return new Rational(mNominator.add(r.mNominator));
            } else {
                // mNominator/mDenominator + r.mNominator/r.mDenominator = (mNominator * r.mDenominator + mDenominator * r.mNominator) / (mDenominator * r.mDenominator)
                final BigInteger n = mNominator.multiply(r.mDenominator).add(mDenominator.multiply(r.mNominator));
                final BigInteger d = mDenominator.multiply(r.mDenominator);
                return new Rational(n, d);
            }
        } else if (summand instanceof Real) {
            return summand.sum(this);
        } else if (summand == Infinity.POSITIVE || summand == Infinity.NEGATIVE) {
            return summand;
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand +
                "' is not a number or string."
            );
        }
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(mNominator.toString());
        if ( ! mIsInteger) {
            sb.append('/');
            sb.append(mDenominator.toString());
        }
    }

    public SetlString charConvert() throws NumberToLargeException {
        if (mNominator.compareTo(BigInteger.valueOf(127)) <= 0 &&
            mNominator.compareTo(BigInteger.ZERO) >= 0         &&
            mIsInteger
           )
        {
            return new SetlString((char) mNominator.intValue());
        } else {
            throw new NumberToLargeException(
                "'" + this + "' is not usable for ASCII conversation" +
                " (it is > 127 or negative or has a denominator != 1)."
            );
        }
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(final Value v){
        if (v instanceof Rational) {
            final Rational r = (Rational) v;
            if (mIsInteger && r.mIsInteger) {
                // a/1 == p/1  <==>  a == p
                return mNominator.compareTo(r.mNominator);
            } else {
                // a/b == p/q  <==>  a * q == b * p
                final BigInteger aq = mNominator.multiply(r.mDenominator);
                final BigInteger bp = mDenominator.multiply(r.mNominator);
                return aq.compareTo(bp);
            }
        } else if (v instanceof Real) {
            return (new Real(mNominator, mDenominator)).compareTo(v);
        } else if (v instanceof SetlError ||
                   v == Om.OM             ||
                   v == Infinity.NEGATIVE ||
                   v == SetlBoolean.TRUE  ||
                   v == SetlBoolean.FALSE
                  )
        {
            // only SetlError, Om, -Infinity and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }

    private final static int initHashCode = Rational.class.hashCode();

    public int hashCode() {
        return (initHashCode + mNominator.hashCode()) * 31 + mDenominator.hashCode();
    }
}


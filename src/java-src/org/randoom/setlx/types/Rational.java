package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.math.BigInteger;

/**
 * This class represents a rational number.
 */
public class Rational extends NumberValue {

    public  final static    Rational    ZERO              = new Rational(0);
    public  final static    Rational    ONE               = new Rational(1);
    public  final static    Rational    TWO               = new Rational(2);
    public  final static    Rational    THREE             = new Rational(3);
    public  final static    Rational    FOUR              = new Rational(4);
    public  final static    Rational    FIVE              = new Rational(5);
    public  final static    Rational    SIX               = new Rational(6);
    public  final static    Rational    SEVEN             = new Rational(7); // of nine?
    public  final static    Rational    EIGHT             = new Rational(8);
    public  final static    Rational    NINE              = new Rational(9);
    public  final static    Rational    TEN               = new Rational(10);

    public  final static    Rational    RAT_BIG           = SetlDouble.bigRational();
    public  final static    Rational    RAT_SMALL         = SetlDouble.smallRational();

    private final static    Rational[]  NUMBERS           = {ZERO,  ONE,  TWO, THREE,
                                                             FOUR,  FIVE, SIX, SEVEN,
                                                             EIGHT, NINE, TEN };

    private final static    BigInteger  INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    private final static    BigInteger  INTEGER_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);

    private final           BigInteger  nominator;
    private final           BigInteger  denominator;
    private final           boolean     isInteger;

    private Rational(final long number) {
        this(BigInteger.valueOf(number));
    }

    private Rational(final BigInteger number) {
        this.nominator      = number;
        this.denominator    = BigInteger.ONE;
        this.isInteger      = true;
    }

    /**
     * This constructor creates a new rational number with nominator n and
     * denominator d.  Care is taken, that the denominator is always positive
     * and that the rational number is in lowest terms.
     *
     * @param n Nominator of the new rational number.
     * @param d Denominator of the new rational number.
     */
    private Rational(BigInteger n, BigInteger d) {
        if (d.signum() == -1) {
            n = n.negate();
            d = d.negate();
        }
        final BigInteger ggt = n.gcd(d);
        this.nominator           = n.divide(ggt);
        this.denominator         = d.divide(ggt);
        this.isInteger           = this.denominator.equals(BigInteger.ONE);
        if (this.denominator.equals(BigInteger.ZERO)) {
            throw new NumberFormatException("new Rational: Division by zero!");
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

    public static Rational valueOf(final long nominator, final long denominator) {
        return new Rational(BigInteger.valueOf(nominator), BigInteger.valueOf(denominator));
    }

    @Override
    public Rational clone() {
        // this value is immutable and can not be changed once set
        return this;
    }

    /*package*/ BigInteger getNominatorValue() {
        return nominator;
    }

    /*package*/ BigInteger getDenominatorValue() {
        return denominator;
    }

    // some constants to speed up isPrime()
    private final static    int[]           SOME_INT_PRIMES     = {
          2,   3,   5,   7,  11,  13,  17,  19,  23,  29,  31,  37,  41,  43,  47,
         53,  59,  61,  67,  71,  73,  79,  83,  89,  97, 101, 103, 107, 109, 113,
        127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197,
        199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281
    };
    private final static    BigInteger[]    SOME_PRIMES         = new BigInteger[SOME_INT_PRIMES.length];
    private final static    BigInteger[]    SOME_PRIMES_SQUARED = new BigInteger[SOME_INT_PRIMES.length];
    static {
        for (int i = 0; i < SOME_PRIMES.length; ++i) {
            SOME_PRIMES[i]         = BigInteger.valueOf(SOME_INT_PRIMES[i]);
            SOME_PRIMES_SQUARED[i] = BigInteger.valueOf(SOME_INT_PRIMES[i] * SOME_INT_PRIMES[i]);
        }
    }

    public boolean isPrime() {
        if ( ! isInteger || nominator.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }
        for (int i = 0; i < SOME_PRIMES.length; ++i) {
            if (nominator.mod(SOME_PRIMES[i]).equals(BigInteger.ZERO)) {
                if (nominator.equals(SOME_PRIMES[i])) {
                    return true;
                } else {
                    return false;
                }
            } else if (SOME_PRIMES_SQUARED[i].compareTo(nominator) >= 0) {
                return true;
            }
        }

        final BigInteger two = SOME_PRIMES[0]; // == BigInteger.valueOf(2)
              BigInteger i   = SOME_PRIMES[SOME_PRIMES.length -1].add(two);
        while (i.multiply(i).compareTo(nominator) <= 0) {
            if (nominator.mod(i).equals(BigInteger.ZERO)) {
                return false;
            }
            i = i.add(two);
        }

        return true;
    }

    public boolean isProbablePrime() {
        if ( ! isInteger ) {
            return false;
        } else if (nominator.compareTo(INTEGER_MAX_VALUE) < 0) {
            return nominator.isProbablePrime(15);
        }
        return nominator.isProbablePrime(30);
    }

    public Rational nextProbablePrime() throws NotAnIntegerException {
        if ( ! isInteger || nominator.compareTo(BigInteger.ZERO) <= 0) {
            throw new NotAnIntegerException(
                "'" + this + "' is not an integer >= 1."
            );
        }
        return new Rational(nominator.nextProbablePrime());
    }

    /* type check (sort of Boolean operation) */

    @Override
    public SetlBoolean isInteger() {
        if (isInteger) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    @Override
    public SetlBoolean isRational() {
        return SetlBoolean.TRUE;
    }

    /* type conversion */

    @Override
    public Rational toInteger(final State state) {
        if (isInteger) {
            return this;
        } else {
            return new Rational(nominator.divide(denominator));
        }
    }

    @Override
    public Rational toRational(final State state) {
        return this;
    }

    @Override
    public SetlDouble toDouble(final State state) throws NumberToLargeException, UndefinedOperationException {
        return SetlDouble.valueOf(nominator, denominator);
    }

    /*package*/ SetlDouble toDouble() throws NumberToLargeException {
        return SetlDouble.valueOf(nominator, denominator);
    }

    /* native type checks */

    @Override
    public boolean jIntConvertable() {
        return (isInteger &&
                nominator.compareTo(INTEGER_MAX_VALUE) < 0 &&
                nominator.compareTo(INTEGER_MIN_VALUE) > 0
               );
    }

    @Override
    public boolean jDoubleConvertable() {
        if (this.compareTo(ZERO) == 0) {
            return true;
        }
        final Rational a = new Rational(nominator.abs(), denominator.abs());
        if (a.compareTo(RAT_BIG) > 0) {
            return false; // too big
        }
        if (a.compareTo(RAT_SMALL) < 0) {
            return false; // too small
        }
        return true;
    }

    /* native type conversions */

    @Override
    public double toJDoubleValue(final State state) throws NumberToLargeException {
        if (!jDoubleConvertable()) {
            final String msg = "The fraction " + nominator + "/" + denominator
                       + "is too big or too small";
            throw new NumberToLargeException(msg);
        }
        final SetlDouble sd = SetlDouble.valueOf(nominator, denominator);
        return sd.jDoubleValue();
    }

    @Override
    public int jIntValue() throws NotAnIntegerException, NumberToLargeException {
        if (!isInteger) {
            throw new NotAnIntegerException(
                "The fraction " + nominator + "/" + denominator + " can't be converted" +
                " to an integer as the denominator is not 1."
            );
        }
        if (nominator.compareTo(INTEGER_MAX_VALUE) > 0 ||
            nominator.compareTo(INTEGER_MIN_VALUE) < 0)
        {
            throw new NumberToLargeException(
                "The value of " + nominator + " is too large or to small for " +
                "this operation."
            );
        } else {
            return nominator.intValue();
        }
    }

    /* arithmetic operations */
    @Override
    public Rational absoluteValue(final State state) {
        return new Rational(nominator.abs(), denominator.abs());
    }

    // The ceil(ing) of a number x is defined as the lowest integer n such that n => x.
    // The calculation is rather complicated because the integer division of negative
    // numbers in Java does not satisfy the mathematical specification that
    // a = (a/b) * b + r with 0 <= r < b.  Rather, Java always rounds to 0.
    @Override
    public Rational ceil(final State state) {
        if (nominator.compareTo(BigInteger.ZERO) > 0 && ! isInteger)
        {
            final BigInteger q = nominator.divide(denominator).add(BigInteger.ONE);
            return new Rational(q);
        }
        return new Rational(nominator.divide(denominator));
    }

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof Rational) {
            final Rational s = (Rational) subtrahend;
            if (isInteger && s.isInteger) {
                // mNominator/1 - s.mNominator/1  <==>  mNominator - s.mNominator
                return new Rational(nominator.subtract(s.nominator));
            } else {
                // mNominator/mDenominator - s.mNominator/s.mDenominator = (mNominator * s.mDenominator - mDenominator * s.mNominator) / (mDenominator * s.mDenominator)
                final BigInteger n = nominator.multiply(s.denominator).subtract(denominator.multiply(s.nominator));
                final BigInteger d = denominator.multiply(s.denominator);
                return new Rational(n, d);
            }
        } else if (subtrahend instanceof SetlDouble) {
            return ((SetlDouble) subtrahend).differenceFlipped(state, this);
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a number."
            );
        }
    }

    // private subclass for threaded factorial computation
    private class Factorial extends Thread {
        private final int        from;
        private final int        to;
        /*package*/   BigInteger result;

        public Factorial(final int from, final int to) {
            this.from   = from;
            this.to     = to;
            this.result = BigInteger.valueOf(from);
        }

        @Override
        public void run() {
            for (int i = from + 1; i < to; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        }
    }

    @Override
    public Rational factorial(final State state) throws SetlException {
        if ( ! isInteger ||
               nominator.compareTo(BigInteger.ZERO) < 0
        ) {
            throw new UndefinedOperationException(
                "'(" + this + ")!' is undefined."
            );
        }
        // The next line will throw an exception if mNominator > 2^31,
        // but wanting that is crazy talk
        final int        n      = jIntValue();
              BigInteger result = BigInteger.ONE;
        final int        CORES  = state.getNumberOfCores();
        // use simple implementation when computing a small factorial or having
        // only one CPU (less overhead)
        if (n <= 512 || CORES <= 1) {
            for (int i = 2; i <= n; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        } else { // use multiple threads for a bigger factorial
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
                } catch (final InterruptedException ie) {
                    throw new StopExecutionException("Interrupted");
                }
            }
        }
        return new Rational(result);
    }

    @Override
    public void fillCollectionWithinRange(final State state,
                                          final Value step_,
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
            for (; i.compareTo(stop) <= 0; i = (Rational) i.sum(state, step)) {
                if (state.isExecutionStopped) {
                    throw new StopExecutionException("Interrupted");
                }
                collection.addMember(state, i);
            }
        } else if (step.compareTo(Rational.ZERO) < 0) {
            for (; i.compareTo(stop) >= 0; i = (Rational) i.sum(state, step)) {
                if (state.isExecutionStopped) {
                    throw new StopExecutionException("Interrupted");
                }
                collection.addMember(state, i);
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
    @Override
    public Rational floor(final State state) {
        if (nominator.compareTo(BigInteger.ZERO) < 0 &&
             ! isInteger
           )
        {
            final BigInteger q = nominator.divide(denominator).subtract(BigInteger.ONE);
            return new Rational(q);
        }
        return new Rational(nominator.divide(denominator));
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
    public Rational minus(final State state) {
        return new Rational(nominator.negate(), denominator);
    }

    // The mathematical specification of the modulo function is:
    //     a % b = a - floor(a/b) * b
    @Override
    public Value modulo(final State state, final Value modulo) throws IncompatibleTypeException, SetlException {
        if (modulo instanceof Rational) {
            final Rational b = (Rational) modulo;
            if (isInteger && b.isInteger) {
                if (b.nominator.equals(BigInteger.ZERO)) {
                    throw new UndefinedOperationException("'" + this + " % 0' is undefined.");
                } else {
                    return new Rational(nominator.mod(b.nominator));
                }
            } else {
                final Rational ab = (Rational) this.quotient(state, b);
                return this.difference(state, ab.floor(state).product(state, b));
            }
        } else if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " % " + modulo + "' is not a rational number."
            );
        }
    }

    @Override
    protected Rational power(final State state, final int exponent) throws NumberToLargeException {
        if (exponent >= 0) {
            return new Rational(nominator  .pow(exponent     ), denominator.pow(exponent     ));
        } else {
            return new Rational(denominator.pow(exponent * -1), nominator  .pow(exponent * -1));
        }
    }

    @Override
    protected NumberValue power(final State state, final double exponent) throws SetlException {
        return toDouble(state).power(state, exponent);
    }

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final Rational r = (Rational) multiplier;
            if (isInteger && r.isInteger) {
                // mNominator/1 * r.mNominator/1  <==>  mNominator * r.mNominator
                return new Rational(nominator.multiply(r.nominator));
            } else {
                // mNominator/mDenominator * r.mNominator/r.mDenominator = (mNominator * r.mNominator) / (mDenominator * r.mDenominator)
                return new Rational(nominator.multiply(r.nominator), denominator.multiply(r.denominator));
            }
        } else if (multiplier instanceof SetlDouble ||
                   multiplier instanceof SetlList   ||
                   multiplier instanceof SetlString
        ) {
            return multiplier.product(state, this);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a number, list or string."
            );
        }
    }

    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Rational) {
            final Rational d = (Rational) divisor;
            if (isInteger && d.isInteger) {
                // (mNominator/1) / (d.mNominator/1)  <==>  mNominator / d.mNominator
                if (d.nominator.equals(BigInteger.ZERO)) {
                    throw new UndefinedOperationException("'" + this + " / 0' is undefined.");
                }
                return new Rational(nominator, d.nominator);
            } else {
                // (mNominator/mDenominator) / (d.mNominator/d.mDenominator) = (mNominator * d.mDenominator) / (mDenominator * d.mNominator)
                if (d.nominator.equals(BigInteger.ZERO)) {
                    throw new UndefinedOperationException("'(" + this + ") / 0' is undefined.");
                }
                return new Rational(nominator.multiply(d.denominator), denominator.multiply(d.nominator));
            }
        } else if (divisor instanceof SetlDouble) {
            return ((SetlDouble) divisor).quotientFlipped(state, this);
        } else if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '(" + this + ") / (" + divisor + ")' is not a number."
            );
        }
    }

    @Override
    public Rational rnd(final State state) throws IncompatibleTypeException {
        if (isInteger) {
            BigInteger rnd;
            do {
                rnd = new BigInteger(nominator.bitLength(), state.getRandom());
            } while (rnd.compareTo(nominator.abs()) > 0);
            if (nominator.compareTo(BigInteger.ZERO) < 0) {
                rnd = rnd.negate();
            }
            return new Rational(rnd);
        } else {
            throw new IncompatibleTypeException(
                "Number of choices argument missing."
            );
        }
    }

    @Override
    public Rational rnd(final State state, final Value numberOfChoices) throws SetlException {
        if (numberOfChoices.isInteger() != SetlBoolean.TRUE ||
            numberOfChoices.compareTo(Rational.TWO) < 0
        ) {
            throw new IncompatibleTypeException(
                "Number of choices '" + numberOfChoices + "' is not an integer >= 2."
            );
        } else {
            final Rational choices = (Rational) numberOfChoices.difference(state, Rational.ONE);
            final Rational r       = choices.rnd(state);
            return new Rational(nominator.multiply(r.nominator), denominator.multiply(choices.nominator));
        }
    }

    @Override
    public Rational round(final State state) throws SetlException {
        if (isInteger) {
            return this;
        } else if (nominator.signum() > 0) {
            final BigInteger[] dr = nominator.divideAndRemainder(denominator);
            final BigInteger   a  = dr[0];
            final BigInteger   b  = dr[1];
            final BigInteger   b2 = b.multiply(new BigInteger("2"));
            final int cmp = b2.compareTo(denominator);
            if (cmp < 0) {
                return new Rational(a);
            } else {
                return new Rational(a.add(BigInteger.ONE));
            }
        } else /* if (nominator.signum() <= 0) */ {
            final BigInteger   nn = nominator.negate();
            final BigInteger[] dr = nn.divideAndRemainder(denominator);
            final BigInteger   a  = dr[0];
            final BigInteger   b  = dr[1];
            final BigInteger   b2 = b.multiply(new BigInteger("2"));
            BigInteger   result;
            final int cmp = b2.compareTo(denominator);
            if (cmp < 0) {
                result = a;
            } else {
                result = a.add(BigInteger.ONE);
            }
            return new Rational(result.negate());
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof Rational) {
            final Rational r = (Rational) summand;
            if (isInteger && r.isInteger) {
                // mNominator/1 + r.mNominator/1  <==>  mNominator + r.mNominator
                return new Rational(nominator.add(r.nominator));
            } else {
                // mNominator/mDenominator + r.mNominator/r.mDenominator = (mNominator * r.mDenominator + mDenominator * r.mNominator) / (mDenominator * r.mDenominator)
                final BigInteger n = nominator.multiply(r.denominator).add(denominator.multiply(r.nominator));
                final BigInteger d = denominator.multiply(r.denominator);
                return new Rational(n, d);
            }
        } else if (summand instanceof SetlDouble) {
            return summand.sum(state, this);
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand +
                "' is not a number or string."
            );
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(nominator.toString());
        if ( ! isInteger) {
            sb.append('/');
            sb.append(denominator.toString());
        }
    }

    @Override
    public SetlString charConvert(final State state) throws NumberToLargeException {
        if (nominator.compareTo(BigInteger.valueOf(127)) <= 0 &&
            nominator.compareTo(BigInteger.ZERO) >= 0         &&
            isInteger
           )
        {
            return new SetlString((char) nominator.intValue());
        } else {
            throw new NumberToLargeException(
                "'" + this + "' is not usable for ASCII conversation" +
                " (it is > 127 or negative or has a denominator != 1)."
            );
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof Rational) {
            final Rational r = (Rational) v;
            if (isInteger && r.isInteger) {
                // a/1 == p/1  <==>  a == p
                return nominator.compareTo(r.nominator);
            } else {
                // a/b == p/q  <==>  a * q == b * p
                final BigInteger aq = nominator.multiply(r.denominator);
                final BigInteger bp = denominator.multiply(r.nominator);
                return aq.compareTo(bp);
            }
        } else if (v instanceof SetlDouble) {
            try {
                return toDouble().compareTo(v);
            } catch (final NumberToLargeException e) {
                return this.compareTo(((SetlDouble)v).toRational());
            }
        }  else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    protected int compareToOrdering() {
        return 500;
    }

    @Override
    public boolean equalTo(final Value v) {
        return this.compareTo(v) == 0;
    }

    private final static int initHashCode = Rational.class.hashCode();

    @Override
    public int hashCode() {
        return (initHashCode + nominator.hashCode()) * 31 + denominator.hashCode();
    }
}


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.BaseRunnable;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.math.BigInteger;

/**
 * This class represents a rational number.
 */
public class Rational extends NumberValue {
    /**
     * Rational value of 0.
     */
    public  final static    Rational    ZERO              = new Rational(0);
    /**
     * Rational value of 1.
     */
    public  final static    Rational    ONE               = new Rational(1);
    /**
     * Rational value of 2.
     */
    public  final static    Rational    TWO               = new Rational(2);
    /**
     * Rational value of 3.
     */
    public  final static    Rational    THREE             = new Rational(3);
    /**
     * Rational value of 4.
     */
    public  final static    Rational    FOUR              = new Rational(4);
    /**
     * Rational value of 5.
     */
    public  final static    Rational    FIVE              = new Rational(5);
    /**
     * Rational value of 6.
     */
    public  final static    Rational    SIX               = new Rational(6);
    /**
     * Rational value of Seven of Nine, Tertiary Adjunct of Unimatrix Zero-One.
     */
    public  final static    Rational    SEVEN             = new Rational(7);
    /**
     * Rational value of 8.
     */
    public  final static    Rational    EIGHT             = new Rational(8);
    /**
     * Rational value of 9.
     */
    public  final static    Rational    NINE              = new Rational(9);
    /**
     * Rational value of 10.
     */
    public  final static    Rational    TEN               = new Rational(10);

    private final static    Rational    RAT_BIG           = SetlDouble.bigRational();
    private final static    Rational    RAT_SMALL         = SetlDouble.smallRational();

    private final static    Rational[]  NUMBERS           = {ZERO,  ONE,  TWO, THREE,
                                                             FOUR,  FIVE, SIX, SEVEN,
                                                             EIGHT, NINE, TEN };

    private final static    BigInteger  INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    private final static    BigInteger  INTEGER_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);

    private final           BigInteger  numerator;
    private final           BigInteger  denominator;
    private final           boolean     isInteger;

    private Rational(final long number) {
        this(BigInteger.valueOf(number));
    }

    private Rational(final BigInteger number) {
        this.numerator   = number;
        this.denominator = BigInteger.ONE;
        this.isInteger   = true;
    }

    /**
     * This constructor creates a new rational number with numerator n and
     * denominator d.  Care is taken, that the denominator is always positive
     * and that the rational number is in lowest terms.
     *
     * @param n Numerator of the new rational number.
     * @param d Denominator of the new rational number.
     */
    private Rational(BigInteger n, BigInteger d) {
        if (d.signum() == -1) {
            n = n.negate();
            d = d.negate();
        }
        final BigInteger ggt = n.gcd(d);
        this.numerator   = n.divide(ggt);
        this.denominator = d.divide(ggt);
        this.isInteger   = this.denominator.equals(BigInteger.ONE);
        if (this.denominator.equals(BigInteger.ZERO)) {
            throw new NumberFormatException("new Rational(" + n + ", "+ d + "): Division by zero!");
        }
    }

    /**
     * Get Rational representing the specified value.
     *
     * @param number Numeric value to represent.
     * @return       Rational representation.
     */
    public static Rational valueOf(final long number) {
        if (number >= 0 && number <= 10) {
            return NUMBERS[(int) number];
        }
        return new Rational(number);
    }

    /**
     * Get Rational representing the specified value.
     *
     * @param string Numeric value to represent.
     * @return       Rational representation.
     */
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

    /**
     * Get Rational representing the specified value.
     *
     * @param number Numeric value to represent.
     * @return       Rational representation.
     */
    public static Rational valueOf(final BigInteger number) {
        return new Rational(number);
    }

    /**
     * Get Rational representing the specified value.
     *
     * @param numerator   Numerator of the value to represent.
     * @param denominator Denominator of the value to represent.
     * @return            Rational representation.
     */
    public static Rational valueOf(final BigInteger numerator, final BigInteger denominator) {
        return new Rational(numerator, denominator);
    }

    /**
     * Get Rational representing the specified value.
     *
     * @param numerator   Numerator of the value to represent.
     * @param denominator Denominator of the value to represent.
     * @return            Rational representation.
     */
    public static Rational valueOf(final long numerator, final long denominator) {
        return new Rational(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    @Override
    public Rational clone() {
        // this value is immutable and can not be changed once set
        return this;
    }

    /**
     * Get numerator of the represented value.
     *
     * @return Numerator of the represented value.
     */
    /*package*/ BigInteger getNumeratorValue() {
        return numerator;
    }

    /**
     * Get denominator of the represented value.
     *
     * @return Denominator of the represented value.
     */
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

    /**
     * Test if this is a prime number.
     *
     * @return true if this number is in fact prime.
     */
    public boolean isPrime() {
        if ( ! isInteger || numerator.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }
        for (int i = 0; i < SOME_PRIMES.length; ++i) {
            if (numerator.mod(SOME_PRIMES[i]).equals(BigInteger.ZERO)) {
                return numerator.equals(SOME_PRIMES[i]);
            } else if (SOME_PRIMES_SQUARED[i].compareTo(numerator) >= 0) {
                return true;
            }
        }

        final BigInteger two = SOME_PRIMES[0]; // == BigInteger.valueOf(2)
              BigInteger i   = SOME_PRIMES[SOME_PRIMES.length -1].add(two);
        while (i.multiply(i).compareTo(numerator) <= 0) {
            if (numerator.mod(i).equals(BigInteger.ZERO)) {
                return false;
            }
            i = i.add(two);
        }

        return true;
    }

    /**
     * Estimate if this is a prime number.
     *
     * @return True if this BigInteger is probably prime, false if it's definitely composite.
     */
    public boolean isProbablePrime() {
        if ( ! isInteger ) {
            return false;
        } else if (numerator.compareTo(INTEGER_MAX_VALUE) < 0) {
            return numerator.isProbablePrime(15);
        }
        return numerator.isProbablePrime(30);
    }

    /**
     * Get the next Rational after this number, that is estimated to be prime.
     * The probability that the number returned by this method is composite does
     * not exceed 2<sup>-100</sup>.. This method will never skip over a prime when
     * searching: if it returns p, there is no prime q such that this < q < p.
     *
     * @param  state                 Current state of the running setlX program.
     * @return                       The first Rational greater than this number that is probably prime.
     * @throws NotAnIntegerException Thrown when this number is not an integer.
     */
    public Rational nextProbablePrime(final State state) throws NotAnIntegerException {
        if ( ! isInteger || numerator.compareTo(BigInteger.ZERO) <= 0) {
            throw new NotAnIntegerException(
                "'" + this.toString(state) + "' is not an integer >= 1."
            );
        }
        return new Rational(numerator.nextProbablePrime());
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
            return new Rational(numerator.divide(denominator));
        }
    }

    @Override
    public Rational toRational() {
        return this;
    }

    @Override
    public SetlDouble toDouble() throws NumberToLargeException {
        return SetlDouble.valueOf(numerator, denominator);
    }

    /* native type checks */

    @Override
    public boolean jIntConvertible() {
        return (isInteger &&
                numerator.compareTo(INTEGER_MAX_VALUE) < 0 &&
                numerator.compareTo(INTEGER_MIN_VALUE) > 0
               );
    }

    @Override
    public boolean jDoubleConvertible() {
        if (this.compareTo(ZERO) == 0) {
            return true;
        }
        final Rational a = new Rational(numerator.abs(), denominator.abs());
        return a.compareTo(RAT_BIG) <= 0 && a.compareTo(RAT_SMALL) >= 0;
    }

    /* native type conversions */

    @Override
    public double toJDoubleValue(final State state) throws NumberToLargeException {
        if (!jDoubleConvertible()) {
            final String msg = "The fraction " + numerator + "/" + denominator
                       + "is too big or too small";
            throw new NumberToLargeException(msg);
        }
        final SetlDouble sd = SetlDouble.valueOf(numerator, denominator);
        return sd.jDoubleValue();
    }

    @Override
    public int jIntValue() throws NotAnIntegerException, NumberToLargeException {
        if (!isInteger) {
            throw new NotAnIntegerException(
                "The fraction " + numerator + "/" + denominator + " can't be converted" +
                " to an integer as the denominator is not 1."
            );
        }
        if (numerator.compareTo(INTEGER_MAX_VALUE) > 0 ||
            numerator.compareTo(INTEGER_MIN_VALUE) < 0)
        {
            throw new NumberToLargeException(
                "The value of " + numerator + " is too large or to small for " +
                "this operation."
            );
        } else {
            return numerator.intValue();
        }
    }

    /* arithmetic operations */
    @Override
    public Rational absoluteValue(final State state) {
        return new Rational(numerator.abs(), denominator.abs());
    }

    // The ceil(ing) of a number x is defined as the lowest integer n such that n => x.
    // The calculation is rather complicated because the integer division of negative
    // numbers in Java does not satisfy the mathematical specification that
    // a = (a/b) * b + r with 0 <= r < b.  Rather, Java always rounds to 0.
    @Override
    public Rational ceil(final State state) {
        if (numerator.compareTo(BigInteger.ZERO) > 0 && ! isInteger) {
            final BigInteger q = numerator.divide(denominator).add(BigInteger.ONE);
            return new Rational(q);
        }
        return new Rational(numerator.divide(denominator));
    }

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend.getClass() == Rational.class) {
            final Rational s = (Rational) subtrahend;
            if (isInteger && s.isInteger) {
                // numerator/1 - s.numerator/1  <==>  numerator - s.numerator
                return new Rational(numerator.subtract(s.numerator));
            } else {
                // numerator/denominator - s.numerator/s.denominator = (numerator * s.denominator - denominator * s.numerator) / (denominator * s.denominator)
                final BigInteger n = numerator.multiply(s.denominator).subtract(denominator.multiply(s.numerator));
                final BigInteger d = denominator.multiply(s.denominator);
                return new Rational(n, d);
            }
        } else if (subtrahend.getClass() == SetlDouble.class) {
            return ((SetlDouble) subtrahend).differenceFlipped(state, this);
        } else if (subtrahend.getClass() == Term.class) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " - " + subtrahend.toString(state) + "' is not a number."
            );
        }
    }

    // private subclass for threaded factorial computation
    private static final class Factorial extends BaseRunnable {
        private final int        from;
        private final int        to;
        private final int        runnerIndex;
        private BigInteger       result;

        private Factorial(final State state, final int from, final int to, int runnerIndex) {
            super(state, StackSize.SMALL);
            this.from        = from;
            this.to          = to;
            this.runnerIndex = runnerIndex;
            this.result = BigInteger.valueOf(from);
        }

        @Override
        public void run() {
            for (int i = from + 1; i < to; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        }

        @Override
        public String getThreadName() {
            return "factorial" + runnerIndex;
        }
    }

    @Override
    public Rational factorial(final State state) throws SetlException {
        if ( ! isInteger ||
               numerator.compareTo(BigInteger.ZERO) < 0
        ) {
            throw new UndefinedOperationException(
                "'(" + this.toString(state) + ")!' is undefined."
            );
        }

        // The next line will throw an exception if numerator > 2^31,
        // but wanting that is crazy talk
        final int n = jIntValue();
        BigInteger result = BigInteger.ONE;
        final int CORES = state.getNumberOfCores();
        // use simple implementation when computing a small factorial or having
        // only one CPU (less overhead)
        if (n <= 512 || CORES <= 1) {
            for (int i = 2; i <= n; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        } else { // use multiple threads for a bigger factorial
            // create as many threads as there are processors
            final Factorial[] runners = new Factorial[CORES];
            final Thread[]    threads = new Thread[CORES];
            for (int i = 0; i < CORES; ++i) {
                final int from = n/CORES * (i) + 1;
                      int to   = n/CORES * (i + 1) + 1;
                if (i == CORES - 1) { // last thread
                    to += n % CORES; // pick up the slack
                }
                runners[i] = new Factorial(state, from, to, i);
                threads[i] = runners[i].createThread();
                // start thread
                threads[i].start();
            }
            // wait for them to complete and compile end result
            for (int i = 0; i < CORES; ++i) {
                try {
                    threads[i].join();
                    result = result.multiply(runners[i].result);
                } catch (final InterruptedException ie) {
                    throw new StopExecutionException();
                }
            }
        }
        return new Rational(result);
    }

    @Override
    public void fillCollectionWithinRange(final State state,
                                          final Value step,
                                          final Value stop,
                                          final CollectionValue collection)
        throws SetlException
    {
        Rational stepValue;
        Rational stopValue;

        // check if types make sense (essentially only numbers make sense here)
        if (step.getClass() == Rational.class) {
            stepValue = (Rational) step;
        } else {
            throw new IncompatibleTypeException(
                "Step size '" + step.toString(state) + "' is not a rational number."
            );
        }
        if (stop.getClass() == Rational.class) {
            stopValue = (Rational) stop;
        } else {
            throw new IncompatibleTypeException(
                "Stop argument '" + stop.toString(state) + "' is not a rational number."
            );
        }
        // collect all elements in range
        Rational i = this;
        if (stepValue.compareTo(Rational.ZERO) > 0) {
            for (; i.compareTo(stopValue) <= 0; i = (Rational) i.sum(state, stepValue)) {
                if (state.executionStopped) {
                    throw new StopExecutionException();
                }
                collection.addMember(state, i);
            }
        } else if (stepValue.compareTo(Rational.ZERO) < 0) {
            for (; i.compareTo(stopValue) >= 0; i = (Rational) i.sum(state, stepValue)) {
                if (state.executionStopped) {
                    throw new StopExecutionException();
                }
                collection.addMember(state, i);
            }
        } else { // stepValue == 0!
            throw new UndefinedOperationException(
                "Step size '" + stepValue.toString(state) + "' is illogical."
            );
        }
    }

    // The floor of a number x is defined as the biggest integer n such that n <= x.
    // The calculation is rather complicated because the integer division of negative
    // numbers in Java does not satisfy the mathematical specification that
    // a = (a/b) * b + r with 0 <= r < b.  Rather, Java always rounds to 0.
    @Override
    public Rational floor(final State state) {
        if (numerator.signum() == -1 && ! isInteger) {
            return new Rational(numerator.divide(denominator).subtract(BigInteger.ONE));
        }
        return new Rational(numerator.divide(denominator));
    }

    // The mathematical specification of the integer division is:
    //  for b > 0
    //     a \ b = floor(a/b)
    //  for b < 0
    //     a \ b = -floor(a / (-b))
    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor.isNumber() == SetlBoolean.TRUE) {
            if (((NumberValue) divisor).numericalComparisonTo(Rational.ZERO) > 0) {
                return this.quotient(state, divisor).floor(state);
            } else {
                return this.quotient(state, divisor.minus(state)).floor(state).minus(state);
            }
        } else if (divisor.getClass() == Term.class) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " \\ " + divisor.toString(state) + "' is not a number."
            );
        }
    }

    @Override
    public Rational minus(final State state) {
        return new Rational(numerator.negate(), denominator);
    }

    // The mathematical specification of the modulo function is:
    //  for b > 0
    //     a % b = a - floor(a/b) * b
    //  for b < 0
    //     a % b = a + floor(a / (-b)) * b
    @Override
    public Value modulo(final State state, final Value modulus) throws SetlException {
        if (modulus.getClass() == Rational.class) {
            final Rational b = (Rational) modulus;
            if (isInteger && b.isInteger && b.numerator.equals(BigInteger.ZERO)) {
                throw new UndefinedOperationException("'" + this.toString(state) + " % 0' is undefined.");
            } else if (isInteger && b.isInteger && numerator.signum() == 1) {
                return new Rational(numerator.remainder(b.numerator));
            } else if (b.numerator.signum() == 1) {
                final Rational ab = (Rational) this.quotient(state, b);
                return this.difference(state, ab.floor(state).product(state, b));
            } else /*if (b.numerator.signum() == 1) */ {
                final Rational ab = (Rational) this.quotient(state, b.minus(state));
                return this.sum(state, ab.floor(state).product(state, b));
            }
        } else if (modulus.getClass() == Term.class) {
            return ((Term) modulus).moduloFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                    "Right-hand-side of '" + this.toString(state) + " % " + modulus.toString(state) + "' is not a rational number."
            );
        }
    }

    @Override
    protected Rational power(final State state, final int exponent) throws UndefinedOperationException {
        if (exponent >= 0) {
            return new Rational(numerator.pow(exponent), denominator.pow(exponent     ));
        } else if (ZERO.equals(this)) {
            throw new UndefinedOperationException("'0 ** " + exponent + "' is undefined.");
        }
        return new Rational(denominator.pow(exponent * -1), numerator.pow(exponent * -1));
    }

    @Override
    protected NumberValue power(final State state, final double exponent) throws SetlException {
        return toDouble(state).power(state, exponent);
    }

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier.getClass() == Rational.class) {
            final Rational r = (Rational) multiplier;
            if (isInteger && r.isInteger) {
                // numerator/1 * r.numerator/1  <==>  numerator * r.numerator
                return new Rational(numerator.multiply(r.numerator));
            } else {
                // numerator/denominator * r.numerator/r.denominator = (numerator * r.numerator) / (denominator * r.denominator)
                return new Rational(numerator.multiply(r.numerator), denominator.multiply(r.denominator));
            }
        } else if (multiplier.isDouble() == SetlBoolean.TRUE ||
                   multiplier.isList()   == SetlBoolean.TRUE ||
                   multiplier.isString() == SetlBoolean.TRUE ||
                   multiplier.isMatrix() == SetlBoolean.TRUE ||
                   multiplier.isVector() == SetlBoolean.TRUE
        ) {
            return multiplier.product(state, this);
        } else if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " * " + multiplier.toString(state) + "' is not a number, list or string."
            );
        }
    }

    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor.getClass() == Rational.class) {
            final Rational d = (Rational) divisor;
            if (isInteger && d.isInteger) {
                // (numerator/1) / (d.numerator/1)  <==>  numerator / d.numerator
                if (d.numerator.equals(BigInteger.ZERO)) {
                    throw new UndefinedOperationException("'" + this.toString(state) + " / 0' is undefined.");
                }
                return new Rational(numerator, d.numerator);
            } else {
                // (numerator/denominator) / (d.numerator/d.denominator) = (numerator * d.denominator) / (denominator * d.numerator)
                if (d.numerator.equals(BigInteger.ZERO)) {
                    throw new UndefinedOperationException("'(" + this.toString(state) + ") / 0' is undefined.");
                }
                return new Rational(numerator.multiply(d.denominator), denominator.multiply(d.numerator));
            }
        } else if (divisor.getClass() == SetlDouble.class) {
            return ((SetlDouble) divisor).quotientFlipped(state, this);
        } else if (divisor.getClass() == Term.class) {
            return ((Term) divisor).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '(" + this.toString(state) + ") / (" + divisor.toString(state) + ")' is not a number."
            );
        }
    }

    @Override
    public Rational rnd(final State state) throws IncompatibleTypeException {
        if (isInteger) {
            BigInteger rnd;
            do {
                rnd = new BigInteger(numerator.bitLength(), state.getRandom());
            } while (rnd.compareTo(numerator.abs()) > 0);
            if (numerator.compareTo(BigInteger.ZERO) < 0) {
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
            return new Rational(numerator.multiply(r.numerator), denominator.multiply(choices.numerator));
        }
    }

    @Override
    public Rational round(final State state) {
        if (isInteger) {
            return this;
        } else if (numerator.signum() > 0) {
            final BigInteger[] dr = numerator.divideAndRemainder(denominator);
            final BigInteger   a  = dr[0];
            final BigInteger   b  = dr[1];
            final BigInteger   b2 = b.multiply(new BigInteger("2"));
            final int cmp = b2.compareTo(denominator);
            if (cmp < 0) {
                return new Rational(a);
            } else {
                return new Rational(a.add(BigInteger.ONE));
            }
        } else /* if (numerator.signum() <= 0) */ {
            final BigInteger   nn = numerator.negate();
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
        if (summand.getClass() == Rational.class) {
            final Rational r = (Rational) summand;
            if (isInteger && r.isInteger) {
                // numerator/1 + r.numerator/1  <==>  numerator + r.numerator
                return new Rational(numerator.add(r.numerator));
            } else {
                // numerator/denominator + r.numerator/r.denominator = (numerator * r.denominator + denominator * r.numerator) / (denominator * r.denominator)
                final BigInteger n = numerator.multiply(r.denominator).add(denominator.multiply(r.numerator));
                final BigInteger d = denominator.multiply(r.denominator);
                return new Rational(n, d);
            }
        } else if (summand.isDouble() == SetlBoolean.TRUE) {
            return summand.sum(state, this);
        } else if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " + " + summand.toString(state) +
                "' is not a number or string."
            );
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(numerator.toString());
        if ( ! isInteger) {
            sb.append('/');
            sb.append(denominator.toString());
        }
    }

    @Override
    public SetlString charConvert(final State state) throws NumberToLargeException {
        if (numerator.compareTo(BigInteger.valueOf(127)) <= 0 &&
            numerator.compareTo(BigInteger.ZERO) >= 0         &&
            isInteger
           )
        {
            return new SetlString((char) numerator.intValue());
        } else {
            throw new NumberToLargeException(
                "'" + this.toString(state) + "' is not usable for ASCII conversation" +
                " (it is > 127 or negative or has a denominator != 1)."
            );
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Rational.class) {
            return compareTo((Rational) other);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering()) ? -1 : 1;
        }
    }

    @Override
    public int numericalComparisonTo(final NumberValue other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Rational.class) {
            return compareTo((Rational) other);
        } else {
            try {
                return toDouble().compareTo(other);
            } catch (final NumberToLargeException e) {
                return compareTo(other.toRational());
            }
        }
    }

    private int compareTo(Rational r) {
        if (isInteger && r.isInteger) {
            // a/1 == p/1  <==>  a == p
            return numerator.compareTo(r.numerator);
        } else {
            // a/b == p/q  <==>  a * q == b * p
            final BigInteger aq = numerator.multiply(r.denominator);
            final BigInteger bp = denominator.multiply(r.numerator);
            return aq.compareTo(bp);
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Rational.class);

    @Override
    public final long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object other) {
        return other.getClass() == Rational.class && this.compareTo((Rational) other) == 0;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT + numerator.hashCode()) * 31 + denominator.hashCode();
    }
}

package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SetlInt extends NumberValue {

    private BigInteger mNumber;

    public SetlInt(String s){
        mNumber = new BigInteger(s);
    }

    public SetlInt(int number){
        mNumber = BigInteger.valueOf(number);
    }

    public SetlInt(BigInteger number){
        mNumber = number;
    }

    public SetlInt clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /*package*/ BigInteger getNumber() {
        return mNumber;
    }

    /*package*/ int intValue() throws NumberToLargeException {
        if (mNumber.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || mNumber.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
            throw new NumberToLargeException("'" + mNumber + "' is to large/small for this operation.");
        } else {
            return mNumber.intValue();
        }
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isInteger() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    public SetlInt absoluteValue() {
        return new SetlInt(mNumber.abs());
    }

    public Value add(Value summand) throws SetlException {
        if (summand instanceof SetlInt) {
            return new SetlInt(mNumber.add(((SetlInt) summand).mNumber));
        } else if (summand instanceof Real) {
            return summand.add(this);
        } else if (summand.absoluteValue() == Infinity.POSITIVE) {
            return summand;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a number or string.");
        }
    }

    public NumberValue divide(Value divisor) throws SetlException {
        if (divisor instanceof SetlInt) {
            try {
                return new SetlInt(mNumber.divide(((SetlInt) divisor).mNumber));
            } catch (ArithmeticException ae) {
                throw new UndefinedOperationException("'" + this + " / " + divisor + "' is undefined.");
            }
        } else if (divisor instanceof Real) {
            return ((Real) divisor).divideFlipped(this);
        } else if (divisor == Infinity.POSITIVE) {
            return new SetlInt(0);
        } else if (divisor == Infinity.NEGATIVE) {
            return new SetlInt(-0);
        }  else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " / " + divisor + "' is not a number.");
        }
    }

    // subclass for threaded factorial computation
    class Factorial extends Thread {
        private     int        from;
        private     int        to;
        /*package*/ BigInteger result;

        public Factorial(int from, int to) {
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

    // number of CPU cores
    private static final int CORES = Runtime.getRuntime().availableProcessors();

    public SetlInt factorial() throws SetlException {
        if (mNumber.compareTo(BigInteger.ZERO) < 0) {
            throw new UndefinedOperationException("'fac(" + this + ")', e.g. '" + this + "!' is undefined.");
        }
        int        n         = intValue(); // will throw exception if this is >= 2^31, but wanting that is crazy talk
        BigInteger result    = BigInteger.ONE;
        if (n <= 512 || CORES <= 1) { // use simple implementation when computing small factorials or having only one CPU (less overhead)
            for (int i = 2; i <= n; ++i) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        } else { // use multiple threads for bigger factorials
            // create as many threads as there are processors
            Factorial  threads[] = new Factorial[CORES];
            for (int i = 0; i < CORES; ++i) {
                int from = n/CORES * (i) + 1;
                int to   = n/CORES * (i + 1) +1;
                if (i == CORES -1) { // last thread
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
                } catch (InterruptedException ie) {}
            }
        }
        return new SetlInt(result);
    }

    public void fillCollectionWithinRange(Value step_, Value stop_, CollectionValue collection) throws SetlException {
        SetlInt step = null;
        SetlInt stop = null;

        // check if types make sense (essentially only numbers make sense here)
        if (step_ instanceof SetlInt) {
            step = (SetlInt) step_;
        } else {
            throw new IncompatibleTypeException("Step size '" + step_ + "' is not an integer.");
        }
        if (stop_ instanceof SetlInt) {
            stop = (SetlInt) stop_;
        } else {
            throw new IncompatibleTypeException("Stop argument '" + stop_ + "' is not an integer.");
        }

        // collect all elements in range
        try { // maybe we can get away with using integers
            int stopI = stop.intValue(), stepI = step.intValue();
            for (int i = this.intValue(); i <= stopI; i = i + stepI) {
                collection.addMember(new SetlInt(BigInteger.valueOf(i)));
            }
        } catch (NumberToLargeException ntle) { // maybe not
            BigInteger stopBI = stop.mNumber, stepBI = step.mNumber;
            for (BigInteger i = this.mNumber; i.compareTo(stopBI) <= 0; i = i.add(stepBI)) {
                collection.addMember(new SetlInt(i));
            }
        }
    }

    public SetlInt mod(Value modulo) throws IncompatibleTypeException {
        if (modulo instanceof SetlInt) {
            return new SetlInt(mNumber.mod(((SetlInt) modulo).mNumber));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " % " + modulo + "' is not an integer.");
        }
    }

    public Value multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof SetlInt) {
            return new SetlInt(mNumber.multiply(((SetlInt) multiplier).mNumber));
        } else if (multiplier instanceof Real || multiplier instanceof SetlString || multiplier.absoluteValue() == Infinity.POSITIVE) {
            return multiplier.multiply(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side  of '" + this + " * " + multiplier + "' is not a number or string.");
        }
    }

    public SetlInt negate() {
        return new SetlInt(mNumber.negate());
    }

    public SetlInt power(int exponent) {
        return new SetlInt(mNumber.pow(exponent));
    }

    public NumberValue subtract(Value subtrahend) throws SetlException {
        if (subtrahend instanceof SetlInt) {
            return new SetlInt(mNumber.subtract(((SetlInt) subtrahend).mNumber));
        } else if (subtrahend instanceof Real) {
            return ((Real) subtrahend).subtractFlipped(this);
        } else if (subtrahend.absoluteValue() == Infinity.POSITIVE) {
            return (Infinity) subtrahend.negate();
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a number.");
        }
    }

    /* String and Char operations */

    public SetlString charConvert() throws NumberToLargeException {
        if (mNumber.compareTo(BigInteger.valueOf(127)) <= 0 &&
            mNumber.compareTo(BigInteger.ZERO) >= 0)
        {
            return new SetlString("" + (char) mNumber.intValue());
        } else {
            throw new NumberToLargeException("'" + mNumber + "' is not usable for ASCII conversation (it is >127 or negative).");
        }
    }

    public String toString() {
        return mNumber.toString();
    }

    /* Comparisons */

    /* Compare two Values.  Returns -1 if this value is less than the value given
     * as argument, +1 if its greater and 0 if both values contain the same
     * elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v){
        if (v instanceof SetlInt) {
            SetlInt nr = (SetlInt) v;
            return mNumber.compareTo(nr.mNumber);
        } else if (v instanceof Real) {
            return (new Real(mNumber)).compareTo(v);
        } else if (v == Om.OM || v == Infinity.NEGATIVE || v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) {
            // Om, -Infinity and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

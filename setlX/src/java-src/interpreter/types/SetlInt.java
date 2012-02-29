package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.utilities.Environment;

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

    /* type conversions */

    public SetlInt toInteger() {
        return this;
    }

    public Real toReal() {
        return new Real(mNumber);
    }

    /* arithmetic operations */

    public SetlInt absoluteValue() {
        return new SetlInt(mNumber.abs());
    }

    public Value difference(Value subtrahend) throws SetlException {
        if (subtrahend instanceof SetlInt) {
            return new SetlInt(mNumber.subtract(((SetlInt) subtrahend).mNumber));
        } else if (subtrahend instanceof Real) {
            return ((Real) subtrahend).differenceFlipped(this);
        } else if (subtrahend == Infinity.POSITIVE || subtrahend == Infinity.NEGATIVE) {
            return (Infinity) subtrahend.negate();
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a number.");
        }
    }

    public Value divide(Value divisor) throws SetlException {
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
        } else if (divisor instanceof Term) {
            return ((Term) divisor).divideFlipped(this);
        } else {
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

    public SetlInt factorial() throws SetlException {
        if (mNumber.compareTo(BigInteger.ZERO) < 0) {
            throw new UndefinedOperationException("'" + this + "!' is undefined.");
        }
        int        n        = intValue(); // will throw exception if mNumber > 2^31, but wanting that is crazy talk
        BigInteger result   = BigInteger.ONE;
        final int  CORES    = Environment.getNumberOfCores();
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
            int stopI = stop.intValue(), stepI = step.intValue(), i = this.intValue();
            if (stepI > 0) {
                for (; i <= stopI; i = i + stepI) {
                    collection.addMember(new SetlInt(BigInteger.valueOf(i)));
                }
            } else if (stepI < 0) {
                for (; i >= stopI; i = i + stepI) {
                    collection.addMember(new SetlInt(BigInteger.valueOf(i)));
                }
            } else { // stepI == 0!
                throw new UndefinedOperationException("Step size '" + stepI + "' is illogical.");
            }
        } catch (NumberToLargeException ntle) { // maybe not
            BigInteger stopBI = stop.mNumber, stepBI = step.mNumber, i = this.mNumber;
            if (stepBI.compareTo(BigInteger.ZERO) > 0) {
                for (; i.compareTo(stopBI) <= 0; i = i.add(stepBI)) {
                    collection.addMember(new SetlInt(i));
                }
            } else if (stepBI.compareTo(BigInteger.ZERO) < 0) {
                for (; i.compareTo(stopBI) >= 0; i = i.add(stepBI)) {
                    collection.addMember(new SetlInt(i));
                }
            } else { // stepBI == 0!
                throw new UndefinedOperationException("Step size '" + stepBI + "' is illogical.");
            }
        }
    }

    public Value modulo(Value modulo) throws IncompatibleTypeException {
        if (modulo instanceof SetlInt) {
            return new SetlInt(mNumber.mod(((SetlInt) modulo).mNumber));
        } else if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " % " + modulo + "' is not an integer.");
        }
    }

    public Value multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof SetlInt) {
            return new SetlInt(mNumber.multiply(((SetlInt) multiplier).mNumber));
        } else if (multiplier instanceof Real || multiplier instanceof SetlString || multiplier == Infinity.POSITIVE || multiplier == Infinity.NEGATIVE) {
            return multiplier.multiply(this);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
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

    public Value sum(Value summand) throws SetlException {
        if (summand instanceof SetlInt) {
            return new SetlInt(mNumber.add(((SetlInt) summand).mNumber));
        } else if (summand instanceof Real) {
            return summand.sum(this);
        } else if (summand == Infinity.POSITIVE || summand == Infinity.NEGATIVE) {
            return summand;
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a number or string.");
        }
    }

    /* string and char operations */

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

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v){
        if (v instanceof SetlInt) {
            SetlInt nr = (SetlInt) v;
            return mNumber.compareTo(nr.mNumber);
        } else if (v instanceof Real) {
            return (new Real(mNumber)).compareTo(v);
        } else if (v instanceof SetlError || v == Om.OM || v == Infinity.NEGATIVE || v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) {
            // only SetlError, Om, -Infinity and SetlBoolean are smaller
            return 1;
        } else {
            return -1;
        }
    }
}

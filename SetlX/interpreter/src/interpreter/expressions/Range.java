package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlInt;
import interpreter.types.Value;

import java.math.BigInteger;

public class Range extends Constructor {
    private Expr mStart;
    private Expr mSecond;
    private Expr mStop;

    public Range(Expr start, Expr second, Expr stop) {
        mStart  = start;
        mSecond = second;
        mStop   = stop;
    }

    public void fillCollection(CollectionValue collection) throws SetlException {
        Value       startV = null, secondV = null, stopV = null;
        BigInteger  start  = null, second  = null, stop  = null;
        boolean     bigIntegerRequired = true;

        // check if types make sense (essentially only numbers make sense here)
        startV = mStart.eval();
        if (mSecond != null) {
            secondV = mSecond.eval();
        }
        stopV   = mStop.eval();

        if (startV instanceof SetlInt) {
            start = ((SetlInt) startV).getNumber();
        } else {
            throw new IncompatibleTypeException("Start argument `" + startV + "' is not an integer.");
        }
        if (secondV != null) {
            if (secondV instanceof SetlInt) {
                second = ((SetlInt) secondV).getNumber();
            } else {
                throw new IncompatibleTypeException("Second argument `" + secondV + "' is not an integer.");
            }
        } else {
            second = start.add(BigInteger.ONE);
        }
        if (stopV instanceof SetlInt) {
            stop = ((SetlInt) stopV).getNumber();
        } else {
            throw new IncompatibleTypeException("Stop argument `" + stopV + "' is not an integer.");
        }

        BigInteger  step = second.subtract(start);

        // maybe we can get away without looping over BigIntegers
        if (stop.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
            bigIntegerRequired = false;
        }

        // collect all elements in range
        if (bigIntegerRequired) {
            for (BigInteger i = start; i.compareTo(stop) <= 0; i = i.add(step)) {
                collection.addMember(new SetlInt(i));
            }
        } else {
            int startI = start.intValue(), stopI = stop.intValue(), stepI = step.intValue();
            for (int i = startI; i <= stopI; i = i + stepI) {
                collection.addMember(new SetlInt(BigInteger.valueOf(i)));
            }
        }
    }

    public String toString() {
        String r = mStart.toString();
        if (mSecond != null) {
            r += ", " + mSecond;
        }
        return r + " .. " + mStop;
    }
}



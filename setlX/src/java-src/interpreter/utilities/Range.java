package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.CollectionValue;
import interpreter.types.SetlInt;
import interpreter.types.Value;

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
        Value start = mStart.eval();
        Value step  = null;
        // compute step
        if (mSecond != null) {
            step = mSecond.eval().subtract(start);
        } else {
            step = new SetlInt(1);
        }
        start.fillCollectionWithinRange(step, mStop.eval(), collection);
    }

    public String toString(int tabs) {
        String r = mStart.toString(tabs);
        if (mSecond != null) {
            r += ", " + mSecond.toString(tabs);
        }
        return r + " .. " + mStop.toString(tabs);
    }
}


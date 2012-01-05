package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.CollectionValue;
import interpreter.types.SetlInt;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
range
    : expr (',' expr)? '..' expr
    ;

implemented here as:
      ====      ====        ====
     mStart    mSecond      mStop
*/

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

    /* string operations */

    public String toString(int tabs) {
        String r = mStart.toString(tabs);
        if (mSecond != null) {
            r += ", " + mSecond.toString(tabs);
        }
        return r + " .. " + mStop.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'range");
        result.addMember(mStart.toTerm());
        if (mSecond != null) {
            result.addMember(mSecond.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        result.addMember(mStop.toTerm());
        return result;
    }
}


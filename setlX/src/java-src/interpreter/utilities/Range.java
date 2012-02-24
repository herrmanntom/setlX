package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
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
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = "'range";

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

    public void addToTerm(CollectionValue collection) {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mStart.toTerm());
        if (mSecond != null) {
            result.addMember(mSecond.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        result.addMember(mStop.toTerm());
        collection.addMember(result);
    }

    public static Range TermToRange(Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                Expr start  = TermConverter.valueToExpr(term.getMember(new SetlInt(1)));

                Expr second = null;
                if (! term.getMember(new SetlInt(2)).equals(new SetlString("nil"))) {
                    second  = TermConverter.valueToExpr(term.getMember(new SetlInt(2)));
                }

                Expr stop   = TermConverter.valueToExpr(term.getMember(new SetlInt(3)));
                return new Range(start, second, stop);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}


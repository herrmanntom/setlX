package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
range
    : expr (',' expr)? '..' expr
    ;

implemented here as:
      ====      ====        ====
     mStart    mSecond      mStop
*/

public class Range extends CollectionBuilder {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^range";

    private final Expr mStart;
    private final Expr mSecond;
    private final Expr mStop;

    public Range(final Expr start, final Expr second, final Expr stop) {
        mStart  = start;
        mSecond = second;
        mStop   = stop;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        final Value start = mStart.eval(state);
              Value step  = null;
        // compute step
        if (mSecond != null) {
            step = mSecond.eval(state).difference(state, start);
        } else {
            step = Rational.ONE;
        }
        start.fillCollectionWithinRange(state, step, mStop.eval(state), collection);
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        mStart.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        if (mSecond != null) {
            mSecond.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
        mStop.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        mStart.appendString(state, sb, 0);
        if (mSecond != null) {
            sb.append(", ");
            mSecond.appendString(state, sb, 0);
        }
        sb.append(" .. ");
        mStop.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(state, mStart.toTerm(state));
        if (mSecond != null) {
            result.addMember(state, mSecond.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }
        result.addMember(state, mStop.toTerm(state));

        collection.addMember(state, result);
    }

    /*package*/ static Range termToRange(final Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final Expr start  = TermConverter.valueToExpr(term.firstMember());

                      Expr second = null;
                if (! term.getMember(2).equals(new SetlString("nil"))) {
                    second  = TermConverter.valueToExpr(term.getMember(2));
                }

                final Expr stop   = TermConverter.valueToExpr(term.lastMember());
                return new Range(start, second, stop);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}


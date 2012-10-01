package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

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

public class Range extends Constructor {
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

    public void fillCollection(final CollectionValue collection) throws SetlException {
        final Value start = mStart.eval();
              Value step  = null;
        // compute step
        if (mSecond != null) {
            step = mSecond.eval().difference(start);
        } else {
            step = Rational.ONE;
        }
        start.fillCollectionWithinRange(step, mStop.eval(), collection);
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mStart.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        if (mSecond != null) {
            mSecond.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
        mStop.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb) {
        mStart.appendString(sb, 0);
        if (mSecond != null) {
            sb.append(", ");
            mSecond.appendString(sb, 0);
        }
        sb.append(" .. ");
        mStop.appendString(sb, 0);
    }

    /* term operations */

    public void addToTerm(final CollectionValue collection) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(mStart.toTerm());
        if (mSecond != null) {
            result.addMember(mSecond.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        result.addMember(mStop.toTerm());

        collection.addMember(result);
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
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}


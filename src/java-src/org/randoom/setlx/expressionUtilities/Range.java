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

/**
 * A range between two values, with optional step size.
 *
 * grammar rule:
 * range
 *     : expr (',' expr)? '..' expr
 *     ;
 *
 * implemented here as:
 *       ====      ====        ====
 *      mStart    mSecond      mStop
 */
public class Range extends CollectionBuilder {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Range.class);

    private final Expr start;
    private final Expr second;
    private final Expr stop;

    public Range(final Expr start, final Expr second, final Expr stop) {
        this.start  = start;
        this.second = second;
        this.stop   = stop;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        final Value start = this.start.eval(state);
              Value step  = null;
        // compute step
        if (second != null) {
            step = second.eval(state).difference(state, start);
        } else {
            step = Rational.ONE;
        }
        start.fillCollectionWithinRange(state, step, stop.eval(state), collection);
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        start.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        if (second != null) {
            second.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
        stop.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        start.appendString(state, sb, 0);
        if (second != null) {
            sb.append(", ");
            second.appendString(state, sb, 0);
        }
        sb.append(" .. ");
        stop.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(state, start.toTerm(state));
        if (second != null) {
            result.addMember(state, second.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }
        result.addMember(state, stop.toTerm(state));

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


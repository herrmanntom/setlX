package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
equivalence
    : expr '<!=>' expr
    ;

implemented here as:
      ====        ====
      mLhs        mRhs
*/

public class BoolUnEqual extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "^boolUnEqual";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1100;

    private final Expr mLhs;
    private final Expr mRhs;

    public BoolUnEqual(final Expr lhs, final Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        try {
            return mLhs.eval(state).isEqualTo(state, mRhs.eval(state)).negation(state);
        } catch (final SetlException se) {
            se.addToTrace("Error in substitute comparison \"!(" + mLhs + " == " + mRhs + ")\":");
            throw se;
        }
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        mLhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mRhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mLhs.appendString(state, sb, tabs);
        sb.append(" <!=> ");
        mRhs.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, mLhs.toTerm(state));
        result.addMember(state, mRhs.toTerm(state));
        return result;
    }

    public static BoolUnEqual termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true , term.lastMember());
            return new BoolUnEqual(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


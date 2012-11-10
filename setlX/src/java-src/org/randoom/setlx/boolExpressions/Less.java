package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
comparison
    : expr '<' expr
    ;

implemented here as:
      ====     ====
      mLhs     mRhs
*/

public class Less extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = "^less";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    private final Expr mLhs;
    private final Expr mRhs;

    public Less(final Expr lhs, final Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    /*
     * Note that these comparisons do not always behave as expected when
     * the compared values are no numbers!
     *
     * For example:
     * Set comparisons are based upon the subset relation
     * a < b
     * is true, when a is a subset of b.
     *
     * However the negation of this comparison
     * !(a < b)
     * is not (in all cases) equal to
     * a >= b
     * because it is possible that a is not a subset of b AND
     * b is neither subset nor equal to a (e.g. a := {1}; b := {2}).
     * In this case
     * !(a < b)
     * would be true, but
     * a >= b
     * would be false.
     *
     * When comparing numbers
     * !(a < b)
     * would always be the same as
     * a >= b
     */

    protected SetlBoolean evaluate(final State state) throws SetlException {
        return mLhs.eval(state).isLessThan(mRhs.eval(state));
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mLhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mRhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mLhs.appendString(sb, tabs);
        sb.append(" < ");
        mRhs.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mLhs.toTerm(state));
        result.addMember(mRhs.toTerm(state));
        return result;
    }

    public static Less termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true , term.lastMember());
            return new Less(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


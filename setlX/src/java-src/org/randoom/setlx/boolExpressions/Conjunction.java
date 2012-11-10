package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
conjunction
    : boolFactor ('&&' boolFactor)*
    ;

implemented here as:
      ==========       ==========
         mLhs             mRhs
*/

public class Conjunction extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^conjunction";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1400;

    private final Expr mLhs;
    private final Expr mRhs;

    public Conjunction(final Expr lhs, final Expr rhs) {
        mLhs = lhs;
        mRhs = rhs;
    }

    protected Value evaluate(final State state) throws SetlException {
        return mLhs.eval(state).conjunction(state, mRhs);
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
        sb.append(" && ");
        mRhs.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mLhs.toTerm(state));
        result.addMember(mRhs.toTerm(state));
        return result;
    }

    public static Conjunction termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true,  term.lastMember());
            return new Conjunction(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


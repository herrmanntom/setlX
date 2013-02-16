package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
assignment
    : assignable ':=' ((assignment)=> assignment | anyExpr)
    ;

implemented here as:
      ==========       ===================================
         mLhs                          mRhs
*/

public class Assignment extends Expr {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = "^assignment";

    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1000;

    private final Expr  mLhs;
    private final Expr  mRhs;

    public Assignment(final Expr lhs, final Expr rhs) {
        mLhs  = lhs;
        mRhs  = rhs;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value assigned = mLhs.assign(state, mRhs.eval(state).clone());

        if (state.traceAssignments) {
            state.outWriteLn("~< Trace: " + mLhs + " := " + assigned + " >~");
        }

        return assigned;
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
        mRhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        // add all found variables to bound by not supplying unboundVariables
        // as this expression is used in an assignment
        mLhs.collectVariablesAndOptimize(boundVariables, boundVariables, boundVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mLhs.appendString(state, sb, tabs);
        sb.append(" := ");
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

    public static Assignment termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new Assignment(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


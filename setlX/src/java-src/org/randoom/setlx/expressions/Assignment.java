package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
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
    public  final static String     FUNCTIONAL_CHARACTER    = "^assignment";
    // Trace all assignments. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean    sTraceAssignments       = false;

    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE              = 1000;

    private final Expr  mLhs;
    private final Expr  mRhs;

    public Assignment(final Expr lhs, final Expr rhs) {
        mLhs  = lhs;
        mRhs  = rhs;
    }

    protected Value evaluate() throws SetlException {
        final Value assigned = mLhs.assign(mRhs.eval().clone());

        if (sTraceAssignments) {
            Environment.outWriteLn("~< Trace: " + mLhs + " := " + assigned + " >~");
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
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mRhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        // add all found variables to bound by not suppliying unboundVariables
        // as this expression is used in an assignment
        mLhs.collectVariablesAndOptimize(boundVariables, boundVariables, boundVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mLhs.appendString(sb, tabs);
        sb.append(" := ");
        mRhs.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static Assignment termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new Assignment(lhs, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


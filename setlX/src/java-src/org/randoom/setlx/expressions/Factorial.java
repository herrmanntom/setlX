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
factor
    : [...]
    | simpleFactor '!'?
    ;

implemented here as:
      ============
         mExpr
*/

public class Factorial extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^factorial";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr mExpr;

    public Factorial(final Expr expr) {
        mExpr = expr;
    }

    protected Value evaluate(final State state) throws SetlException {
        return mExpr.eval(state).factorial(state);
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
        mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mExpr.appendString(sb, tabs);
        sb.append("!");
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(mExpr.toTerm(state));
        return result;
    }

    public static Factorial termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr expr = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            return new Factorial(expr);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


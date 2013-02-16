package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
grammar rule:
simpleFactor
    : '(' expr ')'
    | [...]
    ;

implemented here as:
          ====
          mExpr
*/

public class BracketedExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr mExpr;

    public BracketedExpr(final Expr expr) {
        mExpr = expr;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return mExpr.eval(state);
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
        mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("(");
        mExpr.appendString(state, sb, tabs);
        sb.append(")");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return mExpr.toTerm(state);
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


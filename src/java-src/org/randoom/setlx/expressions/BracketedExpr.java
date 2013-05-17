package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * A wrapper class for another expression, that is mostly used for printing.
 *
 * grammar rule:
 * simpleFactor
 *     : '(' expr ')'
 *     | [...]
 *     ;
 *
 * implemented here as:
 *           ====
 *           mExpr
 */
public class BracketedExpr extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr expr;

    public BracketedExpr(final Expr expr) {
        this.expr = expr;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return expr.eval(state);
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("(");
        expr.appendString(state, sb, tabs);
        sb.append(")");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return expr.toTerm(state);
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


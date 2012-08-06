package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

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

    protected Value evaluate() throws SetlException {
        return mExpr.eval();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("(");
        mExpr.appendString(sb, tabs);
        sb.append(")");
    }

    /* term operations */

    public Value toTerm() {
        return mExpr.toTerm();
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


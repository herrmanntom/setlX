package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | anyExpr ';'
    ;

implemented here as:
      =======
       mExpr
*/

public class ExpressionStatement extends StatementWithPrintableResult {
    private final Expr    mExpr;
    private       boolean mPrintAfterEval;

    public ExpressionStatement(final Expr expression) {
        mExpr           = expression;
        mPrintAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterEval() {
        mPrintAfterEval = true;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        final Value v = mExpr.eval(state);
        if (mPrintAfterEval) {
            printResult(state, v);
        }
        return null;
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        mExpr.appendString(state, sb, tabs);
        sb.append(";");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return mExpr.toTerm(state);
    }
}


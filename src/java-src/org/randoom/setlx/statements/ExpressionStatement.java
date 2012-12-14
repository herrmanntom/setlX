package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
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

    /*package*/ void setPrintAfterEval() {
        mPrintAfterEval = true;
    }

    protected Value exec(final State state) throws SetlException {
        final Value v = mExpr.eval(state);
        if (mPrintAfterEval && (v != Om.OM || !((Om) v).isHidden()) ) {
            Environment.outWriteLn("~< Result: " + v + " >~");
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
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        mExpr.appendString(sb, tabs);
        sb.append(";");
    }

    /* term operations */

    public Value toTerm(final State state) {
        return mExpr.toTerm(state);
    }
}


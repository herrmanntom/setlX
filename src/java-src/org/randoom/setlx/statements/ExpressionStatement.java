package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Statement containing a single expression.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | expr ';'
 *     ;
 *
 * implemented here as:
 *       =======
 *        expr
 */
public class ExpressionStatement extends StatementWithPrintableResult {
    private final Expr    expr;
    private       boolean printAfterEval;

    /**
     * Create a new ExpressionStatement.
     *
     * @param expression Contained expression.
     */
    public ExpressionStatement(final Expr expression) {
        this.expr           = expression;
        this.printAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterExecution() {
        printAfterEval = true;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value v = expr.eval(state);
        if (printAfterEval) {
            printResult(state, v);
        }
        return null;
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        expr.appendString(state, sb, tabs);
        sb.append(";");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        return expr.toTerm(state);
    }
}

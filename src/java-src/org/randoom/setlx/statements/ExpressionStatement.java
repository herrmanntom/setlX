package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;
import java.util.Set;

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

    public ExpressionStatement(final Expr expression) {
        this.expr           = expression;
        this.printAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterEval() {
        printAfterEval = true;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        final Value v = expr.eval(state);
        if (printAfterEval) {
            printResult(state, v);
        }
        return null;
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
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
    public Value toTerm(final State state) {
        return expr.toTerm(state);
    }

    /* Java Code generation */

    @Override
    public void appendJavaCode(
            final State         state,
            final Set<String>   header,
            final StringBuilder code,
            final int           tabs
    ) {
        state.appendLineStart(code, tabs);
        expr.appendJavaCode(state, header, code, tabs);
        code.append(";");
    }
}


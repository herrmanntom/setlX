package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
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
    private final OperatorExpression expr;
    private       boolean printAfterEval;

    /**
     * Create a new ExpressionStatement.
     *
     * @param expression Contained expression.
     */
    public ExpressionStatement(final OperatorExpression expression) {
        this.expr           = expression;
        this.printAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterExecution() {
        printAfterEval = true;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value v = expr.evaluate(state);
        if (printAfterEval) {
            printResult(state, v);
        }
        return null;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
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

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ExpressionStatement.class) {
            return expr.compareTo(((ExpressionStatement) other).expr);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ExpressionStatement.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ExpressionStatement.class) {
            return expr.equals(((ExpressionStatement) obj).expr);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + expr.hashCode();
    }
}

package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Wrapper class, checking if eval() returns a boolean, used in various statements.
 *
 * grammar rule:
 * condition
 *     : expr
 *     ;

 * implemented here as:
 *       ====
 *       expr
 */
public class Condition extends CodeFragment implements Comparable<Condition> {
    private final Expr expr;

    /**
     * Create a new Condition.
     *
     * @param expr Expression to evaluate to Boolean result.
     */
    public Condition(final Expr expr) {
        this.expr = expr;
    }

    /**
     * Evaluate this condition.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlBoolean eval(final State state) throws SetlException {
        final Value v = expr.eval(state);
        if (v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) { // is Boolean value?
            return (SetlBoolean) v;
        } else {
            throw new IncompatibleTypeException("'" + v + "' is not a Boolean value.");
        }
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
        expr.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        return expr.toTerm(state);
    }

    /* comparisons */

    @Override
    public int compareTo(final Condition other) {
        if (this == other) {
            return 0;
        }
        return expr.compareTo(other.expr);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Condition.class) {
            Condition condition = (Condition) obj;
            return expr.equals(condition.expr);
        }
        return false;
    }

    private final static int INIT_HASH_CODE = Condition.class.hashCode();

    @Override
    public int hashCode() {
        return INIT_HASH_CODE + expr.hashCode();
    }
}


package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of assignments to perform at a later point in time.
 */
public class WriteBackAgent {

    private final List<OperatorExpression> expressions;
    private final List<Value> values;

    /**
     * Create a new write-back-agent object.
     *
     * @param size Number of write-back values used.
     */
    public WriteBackAgent(final int size) {
        this.expressions = new ArrayList<OperatorExpression>(size);
        this.values      = new ArrayList<Value>(size);
    }

    /**
     * Add a pair of expression and value to write back.
     *
     * @param expression Expression to assign to.
     * @param value      Value to assign.
     */
    public void add(final OperatorExpression expression, final Value value) {
        this.expressions.add(expression);
        this.values.add(value);
    }

    /**
     * This functions tries to writes the stored values into the variables used
     * in expressions to initialize the parameters of the previously executed
     * SetlDefinition. Hereby 'rw' parameters are possible without pointers...
     * Two types of expressions are supported:
     *   simple variables
     * and
     *   lists of (lists of) simple variables
     * If the expressions used are more complex or it is otherwise not possible
     * to write the values back, the current pair of expr+value is ignored.
     *
     * @param state   Current state of the running setlX program.
     * @param context Context of this assignment for trace.
     */
    public void writeBack(final State state, final String context) {
        final int size = expressions.size();
        for (int i = 0; i < size; ++i) {
            try {
                AAssignableExpression expression = expressions.get(i).convertToAssignable();
                expression.assignUncloned(state, values.get(i).clone(), context);
            } catch (final SetlException se) {
                // assignment failed => just ignore it
            }
        }
    }
}


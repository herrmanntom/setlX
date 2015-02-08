package org.randoom.setlx.assignments;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.utilities.Expression;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * Stack of operators that can be evaluated.
 */
public abstract class AAssignableExpression extends Expression {

    /**
     * Create an assignable expression from an operator expression.
     *
     * @param operatorExpression           Operator stack to convert.
     * @return                             AssignableExpression.
     * @throws UndefinedOperationException if expression can not be converted.
     */
    public static AAssignableExpression convertToAssignable(OperatorExpression operatorExpression) throws UndefinedOperationException {
        throw new IllegalStateException("not implemented");
    }

    /**
     * Gather all bound and unbound variables in this expression and its siblings,
     * when it is used as an assignment.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#collectVariablesAndOptimize(State, List, List, List)
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     * @return true iff this fragment may be optimized if it is constant.
     */
    public abstract boolean collectVariablesWhenAssigned (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
    );

    /**
     * Sets this expression to the given value. Does not clone 'value' and does
     * not return 'value' for chained assignments.
     *
     * @param state          Current state of the running setlX program.
     * @param value          Value to assign.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract void assignUncloned(final State state, final Value value, final String context) throws SetlException;

    /**
     * Sets this expression to the given value. Does not clone 'value' and does
     * not return 'value' for chained assignment.
     * Also checks if the variable is already defined in scopes up to
     * (but EXCLUDING) 'outerScope'.
     * Returns true and sets 'value' if variable is undefined or already equal to 'value'.
     * Returns false, if variable is defined and different from 'value'.
     *
     * @param state          Current state of the running setlX program.
     * @param value          Value to assign.
     * @param outerScope     Root scope of scopes to check.
     * @param checkObjects   Also check objects if they have 'value' set in them.
     * @param context        Context description of the assignment for trace.
     * @return               True, if variable is undefined or already equal to 'value'.
     * @throws org.randoom.setlx.exceptions.SetlException Thrown in case of some (user-) error.
     */
    public abstract boolean assignUnclonedCheckUpTo(
            final State         state,
            final Value         value,
            final VariableScope outerScope,
            final boolean       checkObjects,
            final String        context
    ) throws SetlException;
}

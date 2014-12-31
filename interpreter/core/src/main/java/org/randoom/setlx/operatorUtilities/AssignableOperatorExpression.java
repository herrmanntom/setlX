package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operators.AOperator;
import org.randoom.setlx.operators.IAssignableOperator;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * Stack of operators that can be evaluated.
 */
public class AssignableOperatorExpression extends OperatorExpression {

    private AssignableOperatorExpression(FragmentList<AOperator> operators) {
        super(operators);
    }

    /**
     * Create a assignable operator stack from a normal one.
     *
     * @param operatorExpression           Operator stack to convert.
     * @return                             Assignable operator stack.
     * @throws UndefinedOperationException in case operators are not instances of AOperator.
     */
    public static AssignableOperatorExpression convertToAssignable(OperatorExpression operatorExpression) throws UndefinedOperationException {
        FragmentList<AOperator> operators = operatorExpression.getOperators();
        for (AOperator operator : operators) {
            if (! (operator instanceof IAssignableOperator)) {
                throw new UndefinedOperationException("Trying to convert AOperator that is not an IAssignableOperator!");
            }
        }
        return new AssignableOperatorExpression(operators);
    }

    /**
     * Create a assignable operator stack from a list of operators.
     *
     * @param operator Operator to convert.
     * @return         Assignable operator stack.
     */
    public static <T extends AOperator & IAssignableOperator> AssignableOperatorExpression convertToAssignable(T operator) {
        FragmentList<AOperator> aOperators = new FragmentList<AOperator>(1);
        aOperators.add(operator);
        return new AssignableOperatorExpression(aOperators);
    }

    /**
     * Create a assignable operator stack from a list of operators.
     *
     * @param operators                    Operator stack to convert.
     * @return                             Assignable operator stack.
     * @throws UndefinedOperationException in case operators are not instances of AOperator.
     */
    public static AssignableOperatorExpression convertToAssignable(List<IAssignableOperator> operators) throws UndefinedOperationException {
        FragmentList<AOperator> aOperators = new FragmentList<AOperator>(operators.size());
        for (IAssignableOperator operator : operators) {
            if (operator instanceof AOperator) {
                aOperators.add((AOperator) operator);
            } else {
                throw new UndefinedOperationException("Trying to convert IAssignableOperator that is not an AOperator!");
            }
        }
        return new AssignableOperatorExpression(aOperators);
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
     */
    public void collectVariablesWhenAssigned (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
    ) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Sets this expression to the given value. Does not clone 'value' and does
     * not return 'value' for chained assignments.
     *
     * @param state          Current state of the running setlX program.
     * @param value          Value to assign.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public void assignUncloned(final State state, final Value value, final String context) throws SetlException {
        throw new IllegalStateException("Not implemented");
    }

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
    public boolean assignUnclonedCheckUpTo(
            final State         state,
            final Value         value,
            final VariableScope outerScope,
            final boolean       checkObjects,
            final String        context
    ) throws SetlException {
        throw new UndefinedOperationException(
                "Error in \"" + this + "\":" + state.getEndl() +
                        "This expression can not be used as target for this kind of assignments."
        );
    }
}

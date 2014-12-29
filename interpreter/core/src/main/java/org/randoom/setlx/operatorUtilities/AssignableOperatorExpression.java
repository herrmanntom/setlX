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

    /**
     * Create a new operator stack.
     *
     * @param operators Operator stack to evaluate.
     */
    public <T extends AOperator & IAssignableOperator> AssignableOperatorExpression(FragmentList<T> operators) {
        super(new FragmentList<AOperator>(operators));
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

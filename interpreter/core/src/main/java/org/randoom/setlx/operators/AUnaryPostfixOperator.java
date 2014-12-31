package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Base class for unary postfix operators.
 */
public abstract class AUnaryPostfixOperator extends AOperator {

    @Override
    public final boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public final OptimizerData collectVariables(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, Stack<OptimizerData> optimizerData) {
        OptimizerData lhs = optimizerData.poll();
        return collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables, lhs);
    }

    /**
     * Gather all bound and unbound variables in this operator and its siblings.
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" by this operator.
     * @param unboundVariables Variables not present in bound when used
     * @param usedVariables    Variables present in bound when used
     * @param lhs              Data for optimization from left hand side.
     * @return Data for optimization.
     */
    public OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        return new OptimizerData(
                lhs.isAllowOptimization()
        );
    }

    @Override
    public boolean hasArgumentBeforeOperator() {
        return false;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return true;
    }

    @Override
    public Value buildTerm(State state, Stack<Value> termFragments) throws SetlException {
        Term term = new Term(generateFunctionalCharacter(this.getClass()));
        term.addMember(state, termFragments.poll());
        return modifyTerm(state, term);
    }

    /**
     * Modify default term for this operator.
     *
     * @param state          Current state of the running setlX program.
     * @param term           Term to work with.
     * @return               Resulting term.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value modifyTerm(State state, Term term) throws SetlException {
        return term;
    }
}

package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Base class for unary operators.
 */
public abstract class AUnaryOperator extends AOperator {
    private final String FUNCTIONAL_CHARACTER;

    /** Create a new UnaryOperator **/
    protected AUnaryOperator() {
        FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(this.getClass());
    }

    public int numberOfExpressionsRequiredForOperator() {
        return 0;
    }

    @Override
    public final boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public final OptimizerData collectVariables(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, ArrayDeque<OptimizerData> optimizerData) {
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
    public Value buildTerm(State state, ArrayDeque<Value> termFragments) throws SetlException {
        Term term = new Term(FUNCTIONAL_CHARACTER);
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

    /**
     * Append arguments and the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            OperatorStack to append to.
     * @param operator                 Operator to append to the end.
     * @throws TermConversionException If term is malformed.
     */
    protected static void appendToOperatorStack(State state, Term term, FragmentList<AOperator> operatorStack, AUnaryOperator operator) throws TermConversionException {
        if (term.size() < 1) {
            throw new TermConversionException("malformed " + operator.FUNCTIONAL_CHARACTER);
        } else {
            OperatorExpression.appendFromTerm(state, term.firstMember(), operatorStack);
            operatorStack.add(operator);
        }
    }
}

package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Base class for operators that take no arguments.
 * They are in fact not really operators.
 */
public abstract class AZeroOperator extends AOperator {
    private final String FUNCTIONAL_CHARACTER;

    /** Create a new ZeroOperator **/
    protected AZeroOperator() {
        FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(this.getClass());
    }

    @Override
    public OptimizerData collectVariables(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, ArrayDeque<OptimizerData> optimizerData) {
        return new OptimizerData(
                collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
        );
    }

    @Override
    public boolean hasArgumentBeforeOperator() {
        return false;
    }

    public int numberOfExpressionsRequiredForOperator() {
        return 0;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return false;
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 9999;
    }

    @Override
    public final Value buildTerm(State state, ArrayDeque<Value> termFragments) throws SetlException {
        Term term = new Term(FUNCTIONAL_CHARACTER);
        return modifyTerm(state, term, termFragments);
    }

    /**
     * Modify default term for this operator.
     *
     * @param state          Current state of the running setlX program.
     * @param term           Term to work with.
     * @param termFragments  Stack of term fragments
     * @return               Resulting term.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        return term;
    }
}

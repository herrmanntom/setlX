package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all SetlX operators.
 */
public abstract class AOperator extends ImmutableCodeFragment {
    /**
     * Create an assignable expression from this operator.
     *
     * @param assignable                   assignable
     * @return                             AssignableExpression.
     * @throws UndefinedOperationException if operator can not be converted.
     */
    public AAssignableExpression convertToAssignableExpression(AAssignableExpression assignable) throws UndefinedOperationException {
        throw new UndefinedOperationException("Expression cannot be converted");
    }

    /**
     * Gather all bound and unbound variables in this operator and its input.
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" by this operator.
     * @param unboundVariables Variables not present in bound when used
     * @param usedVariables    Variables present in bound when used
     * @param optimizerData    Stack of data for optimization.
     * @return                 Data for optimization.
     */
    public abstract OptimizerData collectVariables(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, ArrayDeque<OptimizerData> optimizerData);

    /**
     * Evaluate this operator, taking arguments from value stack and returning results.
     *
     * @param state              Current state of the running setlX program.
     * @param values             Value stack to work with.
     * @param operatorExpression Entire operator expression, that is currently evaluated (useful for printing error messages)
     * @param currentStackDepth  Place of this operator in operatorExpression
     * @return                   Result of the evaluation.
     * @throws SetlException     Thrown in case of some (user-) error.
     */
    public abstract Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException;

    /**
     * Does this operator have an argument to print before operator symbol?
     *
     * @return True if argument should be printed.
     */
    public abstract boolean hasArgumentBeforeOperator();

    /**
     * How many arguments this operator needs to be printed "inside" the operator symbol?
     *
     * @return True if argument should be printed.
     */
    public abstract int numberOfExpressionsRequiredForOperator();

    /**
     * Append the operator symbol to given string builder.
     *
     * @param state       Current state of the running setlX program.
     * @param sb          StringBuilder to append to.
     * @param expressions Expressions required to append operator
     */
    public abstract void appendOperatorSign(State state, StringBuilder sb, List<String> expressions);

    /**
     * Does this operator have an argument to print after operator symbol?
     *
     * @return True if argument should be printed.
     */
    public abstract boolean hasArgumentAfterOperator();

    /**
     * Is this operator left associative?
     *
     * @return True if operator is left associative.
     */
    public abstract boolean isLeftAssociative();

    /**
     * Is this operator right associative?
     *
     * @return True if operator is right associative.
     */
    public abstract boolean isRightAssociative();

    /**
     * Precedence level in SetlX-grammar. Manly used for automatic bracket insertion
     * when printing expressions.
     *
     * (See src/grammar/OperatorPrecedences.txt)
     *
     * @return Precedence level.
     */
    public abstract int precedence();

    @Override
    public final void appendString(State state, StringBuilder sb, int tabs) {
        int numberOfExpressionsRequired = numberOfExpressionsRequiredForOperator();
        ArrayList<String> variables = new ArrayList<>(numberOfExpressionsRequired);
        if (numberOfExpressionsRequired > 0) {
            char variableName = 'a';
            for (int i = 0; i < numberOfExpressionsRequired; i++) {
                variables.add(String.valueOf(variableName + i));
            }
        }
        appendOperatorSign(state, sb, variables);
    }

    /**
     * Create term for this operator, taking arguments from term stack and returning results.
     *
     * @param state          Current state of the running setlX program.
     * @param termFragments  Term stack to work with.
     * @return               Resulting term.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract Value buildTerm(State state, ArrayDeque<Value> termFragments) throws SetlException;

    @Override
    public final Value toTerm(State state) throws SetlException {
        throw new IllegalStateException("Not implemented");
    }
}

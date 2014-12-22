package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Base class for all SetlX operators.
 */
public abstract class AOperator {
    /**
     * Evaluate this expression, taking arguments from value stack and returning results.
     *
     * @param state          Current state of the running setlX program.
     * @param values         Value stack to work with.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract Value evaluate(State state, ValueStack values) throws SetlException;

    /**
     * Does this operator have and argument to print before operator symbol?
     *
     * @return True if argument should be printed.
     */
    public abstract boolean hasArgumentBeforeOperator();

    /**
     * Append the operator symbol to given string builder.
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     */
    public abstract void appendOperatorSign(State state, StringBuilder sb);

    /**
     * Does this operator have and argument to print after operator symbol?
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
}

package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

/**
 * Operator that evaluates implication and puts the result on the stack.
 */
public class Implication extends ALazyBinaryInfixOperator {

    /**
     * Create a new Implication operator.
     *
     * @param argument Expression to evaluate lazily.
     */
    public Implication(OperatorExpression argument) {
        super(argument);
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        return values.poll().disjunction(state, getRightHandSide());
    }

    @Override
    public String getOperatorSign() {
        return " || ";
    }

    /**
     * Append the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            Operator to append to.
     * @throws TermConversionException If term is malformed.
     */
    public static void appendToOperatorStack(final State state, final Term term, FragmentList<AOperator> operatorStack) throws TermConversionException {
        Implication implication = new Implication(OperatorExpression.createFromTerm(state, term.lastMember()));
        appendToOperatorStack(state, term, operatorStack, implication);
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public boolean isRightAssociative() {
        return true;
    }

    @Override
    public int precedence() {
        return 1200;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Implication.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + getArgumentHashCode();
    }
}

package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;

/**
 * Operator that evaluates conjunction and puts the result on the stack.
 */
public class Conjunction extends ALazyBinaryInfixOperator {

    private static final String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Conjunction.class);

    /**
     * Create a new Conjunction operator.
     *
     * @param argument Expression to evaluate lazily.
     */
    public Conjunction(OperatorExpression argument) {
        super(argument);
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        return values.poll().conjunction(state, getRightHandSide());
    }

    @Override
    public String getOperatorSign() {
        return " && ";
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
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Conjunction conjunction = new Conjunction(OperatorExpression.createFromTerm(state, term.lastMember()));
            appendToOperatorStack(state, term, operatorStack, conjunction);
        }
    }

    @Override
    public boolean isLeftAssociative() {
        return true;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 1400;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Conjunction.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + getArgumentHashCode();
    }
}

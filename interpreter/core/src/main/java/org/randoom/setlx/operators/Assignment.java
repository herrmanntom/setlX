package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Operator that gets a variable from the current scope and puts it on the stack.
 */
public class Assignment extends AUnaryPrefixOperator {
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Assignment.class);

    private final AAssignableExpression assignableExpression;

    /**
     * Create a new Assignment operator.
     *
     * @param assignableExpression expression to assign to.
     */
    public Assignment(AAssignableExpression assignableExpression) {
        this.assignableExpression = assignableExpression;
    }

    @Override
    public OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        assignableExpression.collectVariablesWhenAssigned(state, boundVariables, unboundVariables, usedVariables);
        return new OptimizerData(
                false
        );
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        Value value = values.poll().clone();
        assignableExpression.assignUncloned(state, value, FUNCTIONAL_CHARACTER);
        return value.clone();
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        assignableExpression.appendString(state, sb, 0);
        sb.append(" := ");
    }

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        Value rightHandSide = term.firstMember();
        term.setMember(state, 1, assignableExpression.toTerm(state));
        term.setMember(state, 2, rightHandSide);
        return term;
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
        try {
            OperatorExpression.appendFromTerm(state, term.lastMember(), operatorStack);
            AAssignableExpression assignableExpression = OperatorExpression.createFromTerm(state, term.firstMember()).convertToAssignable();
            operatorStack.add(new Assignment(assignableExpression));
        } catch (UndefinedOperationException e) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER, e);
        }
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
        return 1000;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Assignment.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Assignment.class) {
            final Assignment otr = (Assignment) other;
            return assignableExpression.compareTo(otr.assignableExpression);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj.getClass() == Assignment.class && assignableExpression.equals(((Assignment) obj).assignableExpression);
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + assignableExpression.hashCode();
    }
}

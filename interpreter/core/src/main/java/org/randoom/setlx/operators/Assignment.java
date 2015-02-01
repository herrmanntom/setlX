package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * Operator that gets a variable from the current scope and puts it on the stack.
 */
public class Assignment extends AUnaryPrefixOperator {
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Assignment.class);

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
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value value = values.poll().clone();
        assignableExpression.assignUncloned(state, value, FUNCTIONAL_CHARACTER);
        return value.clone();
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        assignableExpression.appendString(state, sb, 0);
        sb.append(" := ");
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

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        term.addMember(state, assignableExpression.toTerm(state));
        return term;
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

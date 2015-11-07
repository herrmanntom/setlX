package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.assignments.AssignableMember;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * Operator that load the the last object from the stack and put its given member on the stack.
 */
public class MemberAccess extends AUnaryPostfixOperator {

    private final String id;

    /**
     * Create a new MemberAccess expression.
     *
     * @param variable ID/name of the accessed member.
     */
    public MemberAccess(final Variable variable) {
        this.id = variable.getId();
    }

    @Override
    public AAssignableExpression convertToAssignableExpression(AAssignableExpression assignable) throws UndefinedOperationException {
        if (assignable != null) {
            return new AssignableMember(assignable, new Variable(id));
        } else {
            throw new UndefinedOperationException("Expression cannot be converted");
        }
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        return values.poll().getObjectMemberUnCloned(state, id);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(".");
        sb.append(id);
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
        return 2100;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(MemberAccess.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == MemberAccess.class) {
            final MemberAccess otr = (MemberAccess) other;
            return id.compareTo(otr.id);
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
        return this == obj || obj.getClass() == MemberAccess.class && id.equals(((MemberAccess) obj).id);
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT + id.hashCode();
    }
}

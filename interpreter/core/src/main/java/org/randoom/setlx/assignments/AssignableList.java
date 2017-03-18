package org.randoom.setlx.assignments;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * Assignment to a list of expressions.
 */
public class AssignableList extends AAssignableExpression {

    private final FragmentList<AAssignableExpression> assignableExpressions;

    /**
     * Create a new AssignableList expression.
     *
     * @param assignableExpressions Expressions to assign.
     */
    public AssignableList(final FragmentList<AAssignableExpression> assignableExpressions) {
        this.assignableExpressions = assignableExpressions;
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        boolean optimiseIfConstant = true;
        for (AAssignableExpression assignableExpression : assignableExpressions) {
            optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && optimiseIfConstant;
        }
        return optimiseIfConstant;
    }

    @Override
    public boolean collectVariablesWhenAssigned(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        boolean optimiseIfConstant = true;
        for (AAssignableExpression assignableExpression : assignableExpressions) {
            optimiseIfConstant = assignableExpression.collectVariablesWhenAssigned(state, boundVariables, unboundVariables, usedVariables)
                    && optimiseIfConstant;
        }
        return optimiseIfConstant;
    }

    @Override
    public Value evaluate(State state) throws SetlException {
        SetlList result = new SetlList(assignableExpressions.size());
        for (AAssignableExpression assignableExpression : assignableExpressions) {
            result.addMember(state, assignableExpression.evaluate(state));
        }
        return result;
    }

    @Override
    public void assignUncloned(State state, Value value, String context) throws SetlException {
        if (!(value instanceof IndexedCollectionValue)) {
            throw new IncompatibleTypeException(
                    "The value '" + value.toString(state) + "' is unusable for assignment to \"" + this.toString(state) + "\"."
            );
        }
        IndexedCollectionValue collection = (IndexedCollectionValue) value;
        final int size = assignableExpressions.size();
        if (collection.size() != size) {
            throw new IncompatibleTypeException(
                    "Members of '" + value + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            final AAssignableExpression assignableExpression = assignableExpressions.get(i);
            assignableExpression.assignUncloned(state, collection.getMember(i + 1), context);
        }
    }

    @Override
    public boolean assignUnclonedCheckUpTo(State state, Value value, VariableScope outerScope, boolean checkObjects, String context) throws SetlException {
        if (!(value instanceof IndexedCollectionValue)) {
            throw new IncompatibleTypeException(
                    "The value '" + value.toString(state) + "' is unusable for assignment to \"" + this.toString(state) + "\"."
            );
        }
        IndexedCollectionValue collection = (IndexedCollectionValue) value;
        final int size = assignableExpressions.size();
        if (collection.size() != size) {
            throw new IncompatibleTypeException(
                    "Members of '" + collection + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            final AAssignableExpression assignableExpression = assignableExpressions.get(i);
            if ( ! assignableExpression.assignUnclonedCheckUpTo(state, collection.getMember(i + 1), outerScope, checkObjects, context)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        sb.append("[");

        assignableExpressions.appendString(state, sb);

        sb.append("]");
    }

    @Override
    public Value toTerm(State state) throws SetlException {
        final SetlList list = new SetlList();
        for (AAssignableExpression assignableExpression : assignableExpressions) {
            list.addMember(state, assignableExpression.toTerm(state));
        }
        return list;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(AssignableList.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == AssignableList.class) {
            final AssignableList otr = (AssignableList) other;
            return assignableExpressions.compareTo(otr.assignableExpressions);
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
        return this == obj || obj.getClass() == AssignableList.class && assignableExpressions.equals(((AssignableList) obj).assignableExpressions);
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + assignableExpressions.hashCode();
    }
}

package org.randoom.setlx.assignments;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.CollectionAccess;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignment into a collection.
 */
public class AssignableCollectionAccess extends AAssignableExpression {
    private final AAssignableExpression            leftHandSide;
    private final FragmentList<OperatorExpression> arguments;
    private       Value                            term;

    /**
     * Create a new AssignableCollectionAccess expression.
     *
     * @param leftHandSide Left hand side.
     * @param arguments    Arguments.
     */
    public AssignableCollectionAccess(final AAssignableExpression leftHandSide, final FragmentList<OperatorExpression> arguments) {
        this.leftHandSide = leftHandSide;
        this.arguments = arguments;
        this.term = null;
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        boolean optimiseIfConstant = leftHandSide.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        for (OperatorExpression assignableExpression : arguments) {
            optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && optimiseIfConstant;
        }
        return optimiseIfConstant;
    }

    @Override
    public boolean collectVariablesWhenAssigned(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        // lhs & args are read, not bound, so use collectVariablesAndOptimize()
        boolean optimiseIfConstant = leftHandSide.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        for (OperatorExpression assignableExpression : arguments) {
            optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && optimiseIfConstant;
        }
        return optimiseIfConstant;
    }

    @Override
    public Value evaluate(State state) throws SetlException {
        final Value lhs = this.leftHandSide.evaluate(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side \"" + this.leftHandSide.toString(state) + "\" is undefined."
            );
        }

        // evaluate all arguments
        List<Value> arguments = new ArrayList<>(this.arguments.size());
        for (final OperatorExpression expression: this.arguments) {
            arguments.add(expression.evaluate(state).clone());
        }
        if (arguments.size() > 1) {
            SetlList argumentList = new SetlList(arguments.size());
            for (Value arg : arguments) {
                argumentList.addMember(state, arg);
            }
            arguments = new ArrayList<>(1);
            arguments.add(argumentList);
        }

        // execute
        return lhs.collectionAccessUnCloned(state, arguments);
    }

    @Override
    public void assignUncloned(State state, Value value, String context) throws SetlException {
        final Value lhs = this.leftHandSide.evaluate(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side \"" + this.leftHandSide.toString(state) + "\" is undefined."
            );
        }

        if (arguments.size() > 1) {
            // evaluate all arguments
            List<Value> arguments = new ArrayList<>(this.arguments.size());
            for (final OperatorExpression arg: this.arguments) {
                arguments.add(arg.evaluate(state).clone());
            }
            SetlList argumentList = new SetlList(arguments.size());
            for (Value arg : arguments) {
                argumentList.addMember(state, arg);
            }
            lhs.setMember(state, argumentList, value);
        } else {
            lhs.setMember(state, arguments.get(0).evaluate(state), value);
        }
    }

    @Override
    public boolean assignUnclonedCheckUpTo(State state, Value value, VariableScope outerScope, boolean checkObjects, String context) throws SetlException {
        throw new UndefinedOperationException(
                "Error in \"" + this.toString(state) + "\":" + state.getEndl() +
                        "This expression can not be used as target for this kind of assignments."
        );
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        leftHandSide.appendString(state, sb, tabs);
        sb.append("[");

        arguments.appendString(state, sb);

        sb.append("]");
    }

    @Override
    public Value toTerm(State state) throws SetlException {
        if (term == null) {
            OperatorExpression lhsExpression = OperatorExpression.createFromTerm(state, leftHandSide.toTerm(state));
            OperatorExpression rest = new OperatorExpression(new CollectionAccess(arguments));
            term = new OperatorExpression(lhsExpression, rest).toTerm(state);
        }
        return term;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(AssignableCollectionAccess.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == AssignableCollectionAccess.class) {
            AssignableCollectionAccess otr = (AssignableCollectionAccess) other;
            int cmp = leftHandSide.compareTo(otr.leftHandSide);
            if (cmp != 0) {
                return cmp;
            }
            return arguments.compareTo(otr.arguments);
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
        if (this == obj) {
            return true;
        } else if (obj.getClass() == AssignableCollectionAccess.class) {
            AssignableCollectionAccess other = (AssignableCollectionAccess) obj;
            return leftHandSide.equals(other.leftHandSide) && arguments.equals(other.arguments);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + leftHandSide.hashCode();
        return hash * 31 + arguments.hashCode();
    }
}

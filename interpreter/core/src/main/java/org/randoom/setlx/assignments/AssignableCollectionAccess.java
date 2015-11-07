package org.randoom.setlx.assignments;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Assignment to a list of expressions.
 */
public class AssignableCollectionAccess extends AAssignableExpression {
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(AssignableCollectionAccess.class);

    private final AAssignableExpression            assignableExpression;
    private final FragmentList<OperatorExpression> expressions;

    /**
     * Create a new AssignableCollectionAccess expression.
     *
     * @param assignableExpression Left hand side.
     * @param expressions Arguments.
     */
    public AssignableCollectionAccess(final AAssignableExpression assignableExpression, final FragmentList<OperatorExpression> expressions) {
        this.assignableExpression = unify(assignableExpression);
        this.expressions = unify(expressions);
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        boolean optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        for (OperatorExpression assignableExpression : expressions) {
            optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && optimiseIfConstant;
        }
        return optimiseIfConstant;
    }

    @Override
    public boolean collectVariablesWhenAssigned(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        // lhs & args are read, not bound, so use collectVariablesAndOptimize()
        boolean optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        for (OperatorExpression assignableExpression : expressions) {
            optimiseIfConstant = assignableExpression.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && optimiseIfConstant;
        }
        return optimiseIfConstant;
    }

    @Override
    public Value evaluate(State state) throws SetlException {
        final Value lhs = this.assignableExpression.evaluate(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side \"" + this.assignableExpression.toString(state) + "\" is undefined."
            );
        }

        // evaluate all arguments
        List<Value> arguments = new ArrayList<Value>(this.expressions.size());
        for (final OperatorExpression expression: this.expressions) {
            arguments.add(expression.evaluate(state).clone());
        }
        if (arguments.size() > 1) {
            SetlList argumentList = new SetlList(arguments.size());
            for (Value arg : arguments) {
                argumentList.addMember(state, arg);
            }
            arguments = new ArrayList<Value>(1);
            arguments.add(argumentList);
        }

        // execute
        return lhs.collectionAccessUnCloned(state, arguments);
    }

    @Override
    public void assignUncloned(State state, Value value, String context) throws SetlException {
        final Value lhs = this.assignableExpression.evaluate(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side \"" + this.assignableExpression.toString(state) + "\" is undefined."
            );
        }

        if (expressions.size() > 1) {
            // evaluate all arguments
            List<Value> arguments = new ArrayList<Value>(this.expressions.size());
            for (final OperatorExpression arg: this.expressions) {
                arguments.add(arg.evaluate(state).clone());
            }
            SetlList argumentList = new SetlList(arguments.size());
            for (Value arg : arguments) {
                argumentList.addMember(state, arg);
            }
            lhs.setMember(state, argumentList, value);
        } else {
            lhs.setMember(state, expressions.get(0).evaluate(state), value);
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
        assignableExpression.appendString(state, sb, tabs);
        sb.append("[");

        final Iterator<OperatorExpression> iter = expressions.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append("]");
    }

    @Override
    public Value toTerm(State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        result.addMember(state, assignableExpression.toTerm(state));

        final SetlList list = new SetlList();
        for (OperatorExpression operatorExpression : expressions) {
            list.addMember(state, operatorExpression.toTerm(state));
        }
        result.addMember(state, list);

        return result;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(AssignableCollectionAccess.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == AssignableCollectionAccess.class) {
            AssignableCollectionAccess otr = (AssignableCollectionAccess) other;
            int cmp = assignableExpression.compareTo(otr.assignableExpression);
            if (cmp != 0) {
                return cmp;
            }
            return expressions.compareTo(otr.expressions);
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
            return assignableExpression.equals(other.assignableExpression) && expressions.equals(other.expressions);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + assignableExpression.hashCode();
        return hash * 31 + expressions.hashCode();
    }
}

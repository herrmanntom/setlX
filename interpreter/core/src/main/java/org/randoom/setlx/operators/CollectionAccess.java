package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.assignments.AssignableCollectionAccess;
import org.randoom.setlx.assignments.AssignableMember;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Operator that gets elements of a collection value and puts the result on the stack.
 */
public class CollectionAccess extends AUnaryPostfixOperator {
    private final FragmentList<OperatorExpression> arguments;
    private final boolean                          argumentsContainRange; // does args contain RangeDummy?

    /**
     * Create a new CollectionAccess operator.
     *
     * @param arguments    Parameters to the call.
     */
    public CollectionAccess(FragmentList<OperatorExpression> arguments) {
        this.arguments             = unify(arguments);
        this.argumentsContainRange = this.arguments.contains(new OperatorExpression(CollectionAccessRangeDummy.CARD));
    }

    @Override
    public AAssignableExpression convertToAssignableExpression(AAssignableExpression assignable) throws UndefinedOperationException {
        if (assignable != null && ! argumentsContainRange) {
            return new AssignableCollectionAccess(assignable, arguments);
        } else {
            throw new UndefinedOperationException("Expression cannot be converted");
        }
    }

    @Override
    public OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        for (final OperatorExpression expr : arguments) {
            expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        return new OptimizerData(
                false
        );
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        final Value lhs = values.poll();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side is undefined (om)."
            );
        }
        // evaluate all arguments
        List<Value> args = new ArrayList<Value>(this.arguments.size());
        for (final OperatorExpression arg: this.arguments) {
            args.add(arg.evaluate(state).clone());
        }
        if ( ! argumentsContainRange && args.size() > 1) {
            SetlList argumentList = new SetlList(args.size());
            for (Value arg : args) {
                argumentList.addMember(state, arg);
            }
            args = new ArrayList<Value>(1);
            args.add(argumentList);
        }
        // execute
        return lhs.collectionAccess(state, args);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append("[");

         arguments.appendString(state, sb);

        sb.append("]");
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

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        // Unbox first argument, if it is a variable
        Value value = term.firstMember();
        if (value.getClass() == Term.class) {
            Term firstMember = (Term) value;
            if (firstMember.getFunctionalCharacter().equalsIgnoreCase(Variable.getFunctionalCharacter())) {
                term.setMember(state, 1, firstMember.firstMember());
            }
        }

        final SetlList args = new SetlList(arguments.size());
        for (final OperatorExpression arg: arguments) {
            args.addMember(state, arg.toTerm(state));
        }
        term.addMember(state, args);

        return term;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(CollectionAccess.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == CollectionAccess.class) {
            final CollectionAccess otr = (CollectionAccess) other;
            if (arguments == otr.arguments) {
                return 0; // clone
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
        } else if (obj.getClass() == CollectionAccess.class) {
            CollectionAccess other = (CollectionAccess) obj;
            if (arguments == other.arguments) {
                return true; // clone
            } else if (arguments.size() == other.arguments.size()) {
                return arguments.equals(other.arguments);
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + arguments.hashCode();
    }
}

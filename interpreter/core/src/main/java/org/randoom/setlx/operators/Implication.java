package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Operator that evaluates implication and puts the result on the stack.
 */
public class Implication extends AUnaryPostfixOperator {
    private final OperatorExpression argument;

    /**
     * Create a new Implication operator.
     *
     * @param argument Expression to evaluate lazily.
     */
    public Implication(OperatorExpression argument) {
        this.argument = unify(argument);
    }

    @Override
    public OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        return new OptimizerData(
                argument.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
        );
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        return values.poll().disjunction(state, argument);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" => ");
        argument.appendString(state, sb, 0);
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

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        term.addMember(state, argument.toTerm(state));
        return term;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Implication.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Implication.class) {
            return argument.compareTo(((Implication) other).argument);
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
        } else if (obj.getClass() == Implication.class) {
            return argument.equals(((Implication) obj).argument);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + argument.hashCode();
    }
}

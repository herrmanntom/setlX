package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Operator that quotes an expression.
 */
public class Quote extends AZeroOperator {
    private final OperatorExpression argument;

    /**
     * Create a new Quote operator.
     *
     * @param argument Expression to evaluate lazily.
     */
    public Quote(OperatorExpression argument) {
        this.argument = unify(argument);
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        argument.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        return true;
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        return argument.toTerm(state);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append("@");
        argument.appendString(state, sb, 0);
    }

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        term.addMember(state, argument.toTerm(state));
        return term;
    }

    @Override
    public boolean isRightAssociative() {
        return true;
    }

    @Override
    public int precedence() {
        return 1900;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Quote.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Quote.class) {
            return argument.compareTo(((Quote) other).argument);
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
        } else if (obj.getClass() == Quote.class) {
            return argument.equals(((Quote) obj).argument);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + argument.hashCode();
    }
}

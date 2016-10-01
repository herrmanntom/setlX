package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.ArrayDeque;
import java.util.List;

/**
 * A simple operator that puts a value on the stack.
 */
public class ValueOperator extends AZeroOperator {

    private final Value value;

    /**
     * Constructor.
     *
     * @param value Contained value.
     */
    public ValueOperator(final Value value) {
        this.value = value;
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return true;
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        return value;
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        value.appendString(state, sb, 0);
    }

    @Override
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        return value.toTerm(state);
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ValueOperator.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ValueOperator.class) {
            final ValueOperator otr = (ValueOperator) other;
            return value.compareTo(otr.value);
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
        return this == obj || obj.getClass() == ValueOperator.class && value.equals(((ValueOperator) obj).value);
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + value.hashCode();
    }
}

package org.randoom.setlx.assignments;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.VariableIgnore;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * Simply ignored assignment ('_' operator).
 */
public class AssignableIgnore extends AAssignableExpression {

    /** Singleton **/
    public static final AssignableIgnore AI = new AssignableIgnore();

    private static Value term = null;

    private AssignableIgnore() {}

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return true;
    }

    @Override
    public boolean collectVariablesWhenAssigned(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return true;
    }

    @Override
    public Value evaluate(State state) throws SetlException {
        return IgnoreDummy.ID;
    }

    @Override
    public void assignUncloned(State state, Value value, String context) throws SetlException {
        // it does nothing
    }

    @Override
    public boolean assignUnclonedCheckUpTo(State state, Value value, VariableScope outerScope, boolean checkObjects, String context) throws SetlException {
        return true;
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        sb.append("_");
    }

    @Override
    public Value toTerm(State state) throws SetlException {
        if (term == null) {
            term = new OperatorExpression(VariableIgnore.VI).toTerm(state);
        }
        return term;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(AssignableIgnore.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        }
        return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT);
    }
}

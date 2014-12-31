package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * This class implements an ignored variable inside an assignable expression.
 *
 * grammar rules:
 * assignable
 *     : variable   | idList      | '_'
 *     ;
 *
 * value
 *     : list | set | atomicValue | '_'
 *     ;
 *
 *                                  ===
 */
public class VariableIgnore extends AZeroOperator implements IAssignableOperator {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(VariableIgnore.class);

    /**
     * Singleton VariableIgnore expression.
     */
    public  final static VariableIgnore VI = new VariableIgnore();

    private VariableIgnore() { }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return true;
    }

    @Override
    public IgnoreDummy evaluate(final State state, Stack<Value> values) throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    /* string operations */

    @Override
    public void appendOperatorSign(final State state, final StringBuilder sb) {
        sb.append("_");
    }

    /* comparisons */

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        }
        return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(VariableIgnore.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        return this == obj;
    }

    @Override
    public final int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


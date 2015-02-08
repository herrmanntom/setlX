package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * this class implements a range token inside the parameters of a CollectionAccess.
 *
 * grammar rules:
 * collectionAccessParams
 *     : expr '..' expr?
 *     | [...]
 *     ;
 *
 *            ====
 */
public class CollectionAccessRangeDummy extends AZeroOperator {
    /**
     * Singleton VariableIgnore expression.
     */
    public  final static CollectionAccessRangeDummy CARD = new CollectionAccessRangeDummy();

    private CollectionAccessRangeDummy() { }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return true;
    }

    @Override
    public RangeDummy evaluate(final State state, Stack<Value> values) throws UndefinedOperationException {
        return RangeDummy.RD;
    }

    /* string operations */

    @Override
    public void appendOperatorSign(final State state, final StringBuilder sb) {
        sb.append(" .. ");
    }

    /* comparisons */

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        }
        return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(CollectionAccessRangeDummy.class);

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
}


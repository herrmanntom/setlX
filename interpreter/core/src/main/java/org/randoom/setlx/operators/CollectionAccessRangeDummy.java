package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
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
    public static final CollectionAccessRangeDummy CARD = new CollectionAccessRangeDummy();
    public static final OperatorExpression CARD_OE = new OperatorExpression(CARD);

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

    /**
     * Append the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            Operator to append to.
     * @throws TermConversionException If term is malformed.
     */
    public static void appendToOperatorStack(final State state, final Term term, FragmentList<AOperator> operatorStack) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + generateFunctionalCharacter(CollectionAccess.class));
        } else {
            operatorStack.add(CARD);
        }
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


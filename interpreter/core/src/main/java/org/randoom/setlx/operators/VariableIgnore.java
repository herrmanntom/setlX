package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.assignments.AssignableIgnore;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
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
public class VariableIgnore extends AZeroOperator {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(VariableIgnore.class);

    /**
     * Singleton VariableIgnore expression.
     */
    public  final static VariableIgnore VI = new VariableIgnore();

    private VariableIgnore() { }

    @Override
    public AAssignableExpression convertToAssignableExpression(AAssignableExpression assignable) throws UndefinedOperationException {
        if (assignable == null) {
            return AssignableIgnore.AI;
        } else {
            throw new UndefinedOperationException("Expression cannot be converted");
        }
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return true;
    }

    @Override
    public IgnoreDummy evaluate(final State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    /* string operations */

    @Override
    public void appendOperatorSign(final State state, final StringBuilder sb, List<String> expressions) {
        sb.append("_");
    }

    /* term operations */

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
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            operatorStack.add(VI);
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


package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * An explicit list of expressions, used to fill collections.
 *
 * grammar rule:
 * explicitList
 *     : anyExpr (',' anyExpr)*
 *     ;
 *
 * implemented here as:
 *       =======......=======
 *               list
 */
public class ExplicitList extends CollectionBuilder {
    private final FragmentList<OperatorExpression> list;

    /**
     * Create new ExplicitList.
     *
     * @param exprList List of expressions to evaluate.
     */
    public ExplicitList(final FragmentList<OperatorExpression> exprList) {
        this.list = exprList;
    }

    @Override
    public FragmentList<AAssignableExpression> convertToAssignableExpressions() throws UndefinedOperationException {
        FragmentList<AAssignableExpression> convertedFragments = new FragmentList<>();
        for (OperatorExpression operatorExpression : list) {
            convertedFragments.add(operatorExpression.convertToAssignable());
        }
        return convertedFragments;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        for (final OperatorExpression e: list) {
            collection.addMember(state, e.evaluate(state));
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        boolean allowOptimization = true;
        for (final OperatorExpression expr : list) {
            allowOptimization = expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;
        }
        return allowOptimization;
    }

    /**
     * Get number of expressions contained in this explicit list.
     *
     * @return Number of expressions contained.
     */
    public int size() {
        return list.size();
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        list.appendString(state, sb);
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) throws SetlException {
        for (final OperatorExpression member: list) {
            collection.addMember(state, member.toTerm(state));
        }
    }

    /**
     * Regenerate ExplicitList from a CollectionValue containing terms representing expressions.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    CollectionValue containing the term representation.
     * @return                         Regenerated ExplicitList.
     * @throws TermConversionException in case the term is malformed.
     */
    public static ExplicitList collectionValueToExplicitList(final State state, final CollectionValue value) throws TermConversionException {
        final FragmentList<OperatorExpression> exprList = new FragmentList<>(value.size());
        for (final Value v : value) {
            exprList.add(OperatorExpression.createFromTerm(state, v));
        }
        return new ExplicitList(exprList);
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other, boolean ordered) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ExplicitList.class || other instanceof ExplicitList) {
            return list.compareTo(((ExplicitList) other).list, ordered);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ExplicitList.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj, boolean ordered) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ExplicitList.class || obj instanceof ExplicitList) {
            return list.equals(((ExplicitList) obj).list, ordered);
        }
        return false;
    }

    @Override
    public int computeHashCode(boolean ordered) {
        return  ((int) COMPARE_TO_ORDER_CONSTANT) + list.hashCode(ordered);
    }
}


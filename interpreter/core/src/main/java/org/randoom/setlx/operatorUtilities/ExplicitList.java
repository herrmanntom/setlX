package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Iterator;
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
        this.list = unify(exprList);
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
            allowOptimization = allowOptimization && expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
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
        final Iterator<OperatorExpression> iter = list.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
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
     */
    public static ExplicitList collectionValueToExplicitList(final State state, final CollectionValue value) {
        final FragmentList<OperatorExpression> exprList = new FragmentList<OperatorExpression>(value.size());
        for (final Value v : value) {
            exprList.add(TermConverter.valueToExpr(state, v));
        }
        return new ExplicitList(exprList);
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ExplicitList.class || other instanceof ExplicitList) {
            return list.compareTo(((ExplicitList) other).list);
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ExplicitList.class || obj instanceof ExplicitList) {
            return list.equals(((ExplicitList) obj).list);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return  ((int) COMPARE_TO_ORDER_CONSTANT) + list.hashCode();
    }
}


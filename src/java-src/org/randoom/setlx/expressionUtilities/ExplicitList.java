package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.AssignableExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
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
    private final List<Expr> list;

    /**
     * Create new ExplicitList.
     *
     * @param exprList List of expressions to evaluate.
     */
    public ExplicitList(final List<Expr> exprList) {
        this.list = exprList;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        for (final Expr e: list) {
            collection.addMember(state, e.eval(state));
        }
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : list) {
            expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /**
     * Gather all bound and unbound variables in this expression and its siblings,
     * when it is used as an assignment.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#collectVariablesAndOptimize(State, List, List, List)
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     */
    public void collectVariablesWhenAssigned (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : list) {
            if (expr instanceof AssignableExpression) {
                ((AssignableExpression) expr).collectVariablesWhenAssigned(state, boundVariables, unboundVariables, usedVariables);
            }
        }
    }

    /**
     * Sets this list of expressions in this builder to the values contained in
     * the given collection value. Does not clone 'collection' and does
     * not return 'collection' for chained assignments.
     *
     * @param state          Current state of the running setlX program.
     * @param collection     Collection to assign from.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public void assignUncloned(
        final State                  state,
        final IndexedCollectionValue collection,
        final String                 context
    ) throws SetlException {
        final int size = list.size();
        if (collection.size() != size) {
            throw new IncompatibleTypeException(
                "Members of '" + collection + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            final Expr expr = list.get(i);
            if (expr instanceof AssignableExpression) {
                ((AssignableExpression) expr).assignUncloned(state, collection.getMember(i + 1), context);
            } else {
                throw new IncompatibleTypeException(
                    "Members of '" + collection + "' are unusable for list assignment."
                );
            }
        }
    }

    /**
     * Sets this list of expressions in this builder to the values contained in
     * the given collection value. Does not clone 'collection' and does
     * not return 'collection' for chained assignments.
     *
     * Also checks if the variables to be set are already defined in scopes up to
     * (but EXCLUDING) 'outerScope'.
     * Returns true and sets the values, if each variable is undefined or already equal the the value to be set.
     * Returns false, if a variable is defined and different.
     *
     * @param state          Current state of the running setlX program.
     * @param collection     Collection to assign from.
     * @param outerScope     Root scope of scopes to check.
     * @param checkObjects   Also check objects if they have 'value' set in them.
     * @param context        Context description of the assignment for trace.
     * @return               True, if variable is undefined or already equal the the value to be set.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public boolean assignUnclonedCheckUpTo(
        final State                  state,
        final IndexedCollectionValue collection,
        final VariableScope          outerScope,
        final boolean                checkObjects,
        final String                 context
    ) throws SetlException {
        final int size = list.size();
        if (collection.size() != size) {
            throw new IncompatibleTypeException(
                "Members of '" + collection + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            final Expr expr = list.get(i);
            if (expr instanceof AssignableExpression) {
                if ( ! ((AssignableExpression) expr).assignUnclonedCheckUpTo(state, collection.getMember(i + 1), outerScope, checkObjects, context)) {
                    return false;
                }
            } else {
                throw new IncompatibleTypeException(
                    "Members of '" + collection + "' are unusable for list assignment."
                );
            }
        }
        return true;
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
        final Iterator<Expr> iter = list.iterator();
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
        for (final Expr member: list) {
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
        final List<Expr> exprList = new ArrayList<Expr>(value.size());
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
        } else if (other.getClass() == ExplicitList.class) {
            final List<Expr> otherList = ((ExplicitList) other).list;
            if (list == otherList) {
                return 0; // clone
            }
            final Iterator<Expr> iterFirst  = list.iterator();
            final Iterator<Expr> iterSecond = otherList.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                final int cmp = iterFirst.next().compareTo(iterSecond.next());
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (iterFirst.hasNext()) {
                return 1;
            }
            if (iterSecond.hasNext()) {
                return -1;
            }
            return 0;
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
        } else if (obj.getClass() == ExplicitList.class) {
            final List<Expr> otherList = ((ExplicitList) obj).list;
            if (list == otherList) {
                return true; // clone
            } else if (list.size() == otherList.size()) {
                final Iterator<Expr> iterFirst  = list.iterator();
                final Iterator<Expr> iterSecond = otherList.iterator();
                while (iterFirst.hasNext() && iterSecond.hasNext()) {
                    if ( ! iterFirst.next().equals(iterSecond.next())) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int size = list.size();
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + size;
        if (size >= 1) {
            hash = hash * 31 + list.get(0).hashCode();
            if (size >= 2) {
                hash = hash * 31 + list.get(size-1).hashCode();
            }
        }
        return hash;
    }
}


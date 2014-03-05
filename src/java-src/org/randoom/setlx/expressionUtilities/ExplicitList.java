package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.AssignableExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.Value;
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
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : list) {
            expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    /**
     * Gather all bound and unbound variables in this expression and its siblings,
     * when it is used as an assignment.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#collectVariablesAndOptimize(List, List, List)
     *
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     */
    public void collectVariablesWhenAssigned (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : list) {
            if (expr instanceof AssignableExpression) {
                ((AssignableExpression) expr).collectVariablesWhenAssigned(boundVariables, unboundVariables, usedVariables);
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
     * @param context
     * @return               True, if variable is undefined or already equal the the value to be set.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public boolean assignUnclonedCheckUpTo(
        final State                  state,
        final IndexedCollectionValue collection,
        final VariableScope          outerScope,
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
                if ( ! ((AssignableExpression) expr).assignUnclonedCheckUpTo(state, collection.getMember(i + 1), outerScope, context)) {
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
    public void addToTerm(final State state, final CollectionValue collection) {
        for (final Expr member: list) {
            collection.addMember(state, member.toTerm(state));
        }
    }

    public static ExplicitList collectionValueToExplicitList(final CollectionValue value) throws TermConversionException {
        final List<Expr> exprList = new ArrayList<Expr>(value.size());
        for (final Value v : value) {
            exprList.add(TermConverter.valueToExpr(v));
        }
        return new ExplicitList(exprList);
    }
}


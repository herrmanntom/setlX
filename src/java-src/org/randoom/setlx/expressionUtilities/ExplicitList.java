package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
explicitList
    : anyExpr (',' anyExpr)*
    ;

implemented here as:
      =======......=======
             mList
*/

public class ExplicitList extends Constructor {
    private final List<Expr> mList;

    public ExplicitList(final List<Expr> exprList) {
        mList = exprList;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        for (final Expr e: mList) {
            collection.addMember(state, e.eval(state));
        }
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        for (final Expr expr : mList) {
            expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    // sets the variables used to form this list to the variables from the list given as a parameter
    @Override
    public void assignUncloned(
        final State                  state,
        final IndexedCollectionValue collection
    ) throws SetlException {
        final int size = mList.size();
        if (collection.size() != size) {
            throw new IncompatibleTypeException(
                "Members of '" + collection + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            mList.get(i).assignUncloned(state, collection.getMember(i + 1));
        }
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v'. */
    @Override
    public boolean assignUnclonedCheckUpTo(
        final State                  state,
        final IndexedCollectionValue collection,
        final VariableScope          outerScope
    ) throws SetlException {
        final int size = mList.size();
        if (collection.size() != size) {
            throw new IncompatibleTypeException(
                "Members of '" + collection + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            if ( ! mList.get(i).assignUnclonedCheckUpTo(state, collection.getMember(i + 1), outerScope)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return mList.size();
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        final Iterator<Expr> iter = mList.iterator();
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
        for (final Expr member: mList) {
            collection.addMember(state, member.toTerm(state));
        }
    }

    /*package*/ static ExplicitList collectionValueToExplicitList(final CollectionValue value) throws TermConversionException {
        final List<Expr> exprList = new ArrayList<Expr>(value.size());
        for (final Value v : value) {
            exprList.add(TermConverter.valueToExpr(v));
        }
        return new ExplicitList(exprList);
    }
}


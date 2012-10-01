package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
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

    public void fillCollection(final CollectionValue collection) throws SetlException {
        for (final Expr e: mList) {
            collection.addMember(e.eval());
        }
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
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
    public void assignUncloned(final SetlList list) throws SetlException {
        final int size = mList.size();
        if (list.size() != size) {
            throw new IncompatibleTypeException(
                "Members of '" + list + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            mList.get(i).assignUncloned(list.getMember(i + 1));
        }
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v'. */
    public boolean assignUnclonedCheckUpTo(final SetlList list, final VariableScope outerScope) throws SetlException {
        final int size = mList.size();
        if (list.size() != size) {
            throw new IncompatibleTypeException(
                "Members of '" + list + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < size; ++i) {
            if ( ! mList.get(i).assignUnclonedCheckUpTo(list.getMember(i + 1), outerScope)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return mList.size();
    }

    /* string operations */

    public void appendString(final StringBuilder sb) {
        final Iterator<Expr> iter = mList.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
    }

    /* term operations */

    public void addToTerm(final CollectionValue collection) {
        for (final Expr member: mList) {
            collection.addMember(member.toTerm());
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


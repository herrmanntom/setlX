package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.VariableIgnore;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

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

    // sets the variables used to form this list to the variables from the list given as a parameter
    public void assignUncloned(final SetlList list) throws SetlException {
        if (list.size() != mList.size()) {
            throw new IncompatibleTypeException(
                "Members of '" + list + "' are unusable for list assignment."
            );
        }
        for (int i = 0; i < mList.size(); ++i) {
            final Expr  e = mList.get(i);
            Value v = null;
            try {
                v = list.getMember(i + 1);
            } catch (SetlException se) { /* this can not fail at this point */};

            e.assign(v);
        }
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


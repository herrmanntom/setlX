package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressionUtilities.CollectionBuilder;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/*
grammar rules:
list
    : '[' collectionBuilder? ']'
    ;

set
    : '{' collectionBuilder? '}'
    ;

implemented here as:
====      ============
mType       mBuilder
*/

public class SetListConstructor extends Expr {
    public  final static int        LIST        = 23;
    public  final static int        SET         = 42;
    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE  = 9999;

    private final int               mType;
    private final CollectionBuilder mBuilder;

    public SetListConstructor(final int type, final CollectionBuilder constructor) {
        mType    = type;
        mBuilder = constructor;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        if (mType == SET) {
            final SetlSet set = new SetlSet();
            if (mBuilder != null) {
                mBuilder.fillCollection(state, set);
            }
            return set;
        } else /* if (mType == LIST) */ {
            final SetlList list = new SetlList();
            if (mBuilder != null) {
                mBuilder.fillCollection(state, list);
            }
            list.compress();
            return list;
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
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (mBuilder != null) {
            mBuilder.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v) throws SetlException {
        if (v instanceof IndexedCollectionValue) {
            if (mType == LIST && mBuilder != null) {
                mBuilder.assignUncloned(state, (IndexedCollectionValue) v);
            } else {
                throw new UndefinedOperationException(
                    "Only explicit lists can be used as targets for list assignments."
                );
            }
        } else {
            throw new IncompatibleTypeException(
                "The value '" + v + "' is unusable for assignment to \"" + this + "\"."
            );
        }
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v'. */
    @Override
    public boolean assignUnclonedCheckUpTo(final State state, final Value v, final VariableScope outerScope) throws SetlException {
        if (v instanceof IndexedCollectionValue) {
            if (mType == LIST && mBuilder != null) {
               return mBuilder.assignUnclonedCheckUpTo(state, (IndexedCollectionValue) v, outerScope);
            } else {
                throw new UndefinedOperationException(
                    "Only explicit lists can be used as targets for list assignments."
                );
            }
        } else {
            throw new IncompatibleTypeException(
                "The value '" + v + "' is unusable for assignment to \"" + this + "\"."
            );
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (mType == SET) {
            sb.append("{");
        } else /* if (mType == LIST) */ {
            sb.append("[");
        }
        if (mBuilder != null) {
            mBuilder.appendString(state, sb);
        }
        if (mType == SET) {
            sb.append("}");
        } else /* if (mType == LIST) */ {
            sb.append("]");
        }
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final CollectionValue result;
        if (mType == SET) {
            result = new SetlSet();
        } else /* if (mType == LIST) */ {
            result = new SetlList();
        }
        if (mBuilder != null) {
            mBuilder.addToTerm(state, result);
        }
        return result;
    }

    public static SetListConstructor valueToExpr(final Value value) throws TermConversionException {
        if ( ! (value instanceof SetlList || value instanceof SetlSet)) {
            throw new TermConversionException("not a collectionValue");
        } else {
            final CollectionValue cv = (CollectionValue) value;
            if (cv.size() == 0) { // empty
                if (cv instanceof SetlList) {
                    return new SetListConstructor(LIST, null);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(SET,  null);
                }
            } else { // not empty
                final CollectionBuilder c = CollectionBuilder.collectionValueToBuilder(cv);
                if (cv instanceof SetlList) {
                    return new SetListConstructor(LIST, c);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(SET,  c);
                }
            }
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


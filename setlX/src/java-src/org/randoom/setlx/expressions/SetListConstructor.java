package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Constructor;


/*
grammar rules:
list
    : '[' constructor? ']'
    ;

set
    : '{' constructor? '}'
    ;

implemented here as:
====      ============
mType     mConstructor
*/

public class SetListConstructor extends Expr {
    public  final static int    LIST        = 23;
    public  final static int    SET         = 42;
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE  = 9999;

    private final int           mType;
    private final Constructor   mConstructor;

    public SetListConstructor(final int type, final Constructor constructor) {
        mType        = type;
        mConstructor = constructor;
    }

    protected Value evaluate() throws SetlException {
        if (mType == SET) {
            final SetlSet set = new SetlSet();
            if (mConstructor != null) {
                mConstructor.fillCollection(set);
            }
            return set;
        } else /* if (mType == LIST) */ {
            final SetlList list = new SetlList();
            if (mConstructor != null) {
                mConstructor.fillCollection(list);
            }
            list.compress();
            return list;
        }
    }

    // sets this expression to the given value
    public void assignUncloned(final Value v) throws SetlException {
        if (v instanceof SetlList) {
            if (mType == LIST && mConstructor != null) {
                mConstructor.assignUncloned((SetlList) v);
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

    public void appendString(final StringBuilder sb, final int tabs) {
        if (mType == SET) {
            sb.append("{");
        } else /* if (mType == LIST) */ {
            sb.append("[");
        }
        if (mConstructor != null) {
            mConstructor.appendString(sb);
        }
        if (mType == SET) {
            sb.append("}");
        } else /* if (mType == LIST) */ {
            sb.append("]");
        }
    }

    /* term operations */

    public Value toTerm() {
        final CollectionValue result;
        if (mType == SET) {
            result = new SetlSet();
        } else /* if (mType == LIST) */ {
            result = new SetlList();
        }
        if (mConstructor != null) {
            mConstructor.addToTerm(result);
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
                final Constructor c = Constructor.CollectionValueToConstructor(cv);
                if (cv instanceof SetlList) {
                    return new SetListConstructor(LIST, c);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(SET,  c);
                }
            }
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


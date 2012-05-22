package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Term;
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

    private int         mType;
    private Constructor mConstructor;

    public SetListConstructor(int type, Constructor constructor) {
        mType        = type;
        mConstructor = constructor;
    }

    protected Value evaluate() throws SetlException {
        Value result = null;
        if (mType == SET) {
            SetlSet set = new SetlSet();
            if (mConstructor != null) {
                mConstructor.fillCollection(set);
            }
            result = set;
        } else if (mType == LIST) {
            SetlList list = new SetlList();
            if (mConstructor != null) {
                mConstructor.fillCollection(list);
            }
            list.compress();
            result = list;
        } else {
            throw new UndefinedOperationException(
                "This set/list constructor type is undefined."
            );
        }
        return result;
    }

    // sets this expression to the given value
    public Value assign(Value v) throws SetlException {
        if (v instanceof SetlList) {
            if (mType == LIST && mConstructor != null) {
                return mConstructor.assign((SetlList) v);
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

    public String toString(int tabs) {
        String r;
        if (mType == SET) {
            r = "{";
        } else if (mType == LIST) {
            r = "[";
        } else {
            r = "";
        }
        if (mConstructor != null) {
            r += mConstructor.toString(tabs);
        }
        if (mType == SET) {
            r += "}";
        } else if (mType == LIST) {
            r += "]";
        }
        return r;
    }

    /* term operations */

    public Value toTerm() {
        CollectionValue result;
        if (mType == SET) {
            result = new SetlSet();
        } else if (mType == LIST) {
            result = new SetlList();
        } else {
            result = new Term("'undefindedSetListConstructor");
        }
        if (mConstructor != null) {
            mConstructor.addToTerm(result);
        }
        return result;
    }

    public static SetListConstructor valueToExpr(Value value) throws TermConversionException {
        if ( ! (value instanceof SetlList || value instanceof SetlSet)) {
            throw new TermConversionException("not a collectionValue");
        } else {
            CollectionValue cv = (CollectionValue) value;
            if (cv.size() == 0) { // empty
                if (cv instanceof SetlList) {
                    return new SetListConstructor(LIST, null);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(SET,  null);
                }
            } else { // not empty
                Constructor c = Constructor.CollectionValueToConstructor(cv);
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


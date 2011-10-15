package interpreter.expressions;

import interpreter.Environment;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.SetlOm;
import interpreter.types.SetlSet;
import interpreter.types.SetlTuple;
import interpreter.types.Value;

import java.util.List;

public class SetTupleConstructor extends Expr {
    public final static int SET   = 23;
    public final static int TUPLE = 42;

    private int         mType;
    private Constructor mConstructor;

    public SetTupleConstructor(int type, Constructor constructor) {
        mType        = type;
        mConstructor = constructor;
    }

    public Value evaluate() throws SetlException {
        Value result = null;
        if (mType == SET) {
            SetlSet set = new SetlSet();
            if (mConstructor != null) {
                mConstructor.fillCollection(set);
            }
            result = set;
        } else if (mType == TUPLE) {
            SetlTuple tuple = new SetlTuple();
            if (mConstructor != null) {
                mConstructor.fillCollection(tuple);
            }
            tuple.compress();
            result = tuple;
        } else {
            result = SetlOm.OM;
        }
        return result;
    }

    public boolean setIds(SetlTuple tuple) throws SetlException {
        if (mType == TUPLE && mConstructor instanceof ExplicitList) {
            return ((ExplicitList) mConstructor).setIds(tuple);
        } else {
            throw new UndefinedOperationException("Error in '" + this + "':\n"
                                                + "Only explicit tuples of variables are allowed in iterations.");
        }
    }

    public void setIdsToOm() throws UndefinedOperationException {
        if (mType == TUPLE && mConstructor instanceof ExplicitList) {
            ((ExplicitList) mConstructor).setIdsToOm();
        } else {
            throw new UndefinedOperationException("Error in '" + this + "':\n"
                                                + "Only explicit tuples of variables are allowed in iterations.");
        }
    }

    public String toString() {
        String r;
        if (mType == SET) {
            r = "{";
        } else if (mType == TUPLE) {
            r = "[";
        } else {
            r = "";
        }
        if (mConstructor != null) {
            r += mConstructor;
        }
        if (mType == SET) {
            r += "}";
        } else if (mType == TUPLE) {
            r += "]";
        }
        return r;
    }
}



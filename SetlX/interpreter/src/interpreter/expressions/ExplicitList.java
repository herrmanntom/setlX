package interpreter.expressions;

import interpreter.Environment;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlInt;
import interpreter.types.SetlOm;
import interpreter.types.SetlTuple;
import interpreter.types.Value;

import java.util.List;

public class ExplicitList extends Constructor {
    private List<Expr> mList;

    public ExplicitList(List<Expr> exprList) {
        mList   = exprList;
    }

    public void fillCollection(CollectionValue collection) throws SetlException {
        for (Expr e: mList) {
            collection.addMember(e.eval());
        }
    }

    public boolean setIds(SetlTuple tuple) throws SetlException {
        if (tuple.size() != mList.size()) {
            return false;
        }
        for (int i = 0; i < mList.size(); ++i) {
            Expr  e = mList.get(i);
            Value v = tuple.getMember(new SetlInt(i + 1));
            if (e instanceof Variable) {
                Environment.putValue(((Variable)e).getId(), v.clone());
            } else if (e instanceof SetTupleConstructor) {
                if (v instanceof SetlTuple) {
                    ((SetTupleConstructor) e).setIds((SetlTuple) v);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void setIdsToOm() throws UndefinedOperationException {
        for (int i = 0; i < mList.size(); ++i) {
            Expr  e = mList.get(i);
            if (e instanceof Variable) {
                Environment.putValue(((Variable)e).getId(), SetlOm.OM);
            } else if (e instanceof SetTupleConstructor) {
                ((SetTupleConstructor) e).setIdsToOm();
            } else {
                throw new UndefinedOperationException("Error in '" + this + "':\n"
                                                +     "Only explicit tuples of variables are allowed in iterations.");
            }
        }
    }

    public int size() {
        return mList.size();
    }

    public String toString() {
        String r = "";
        for (Expr e: mList) {
            if (!r.equals("")) {
                r += ", ";
            }
            r += e;
        }
        return r;
    }
}



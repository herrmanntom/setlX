package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.expressions.SetListConstructor;
import interpreter.expressions.Variable;
import interpreter.types.CollectionValue;
import interpreter.types.SetlInt;
import interpreter.types.SetlList;
import interpreter.types.SetlOm;
import interpreter.types.Value;
import interpreter.utilities.Environment;

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

    public boolean setIds(SetlList list) throws SetlException {
        if (list.size() != mList.size()) {
            return false;
        }
        for (int i = 0; i < mList.size(); ++i) {
            Expr  e = mList.get(i);
            Value v = list.getMember(new SetlInt(i + 1));
            if (e instanceof Variable) {
                Environment.putValue(((Variable)e).getId(), v.clone());
            } else if (e instanceof SetListConstructor) {
                if (v instanceof SetlList) {
                    ((SetListConstructor) e).setIds((SetlList) v);
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
            } else if (e instanceof SetListConstructor) {
                ((SetListConstructor) e).setIdsToOm();
            } else {
                throw new UndefinedOperationException("Error in '" + this + "':\n"
                                                +     "Only explicit lists of variables are allowed in iterations.");
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


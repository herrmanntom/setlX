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

    // sets the variables used to form this list to the variables from the list given as a parameter
    public boolean setIds(SetlList list) throws UndefinedOperationException {
        if (list.size() != mList.size()) {
            return false;
        }
        for (int i = 0; i < mList.size(); ++i) {
            Expr  e = mList.get(i);
            Value v = null;
            try {
                list.getMember(new SetlInt(i + 1));
            } catch (SetlException se) { /* this can not fail at this point */};
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

    public int size() {
        return mList.size();
    }

    public String toString(int tabs) {
        String r = "";
        for (Expr e: mList) {
            if (!r.equals("")) {
                r += ", ";
            }
            r += e.toString(tabs);
        }
        return r;
    }
}


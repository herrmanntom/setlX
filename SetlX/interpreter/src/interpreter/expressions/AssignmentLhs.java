package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlList;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.LinkedList;
import java.util.List;

public class AssignmentLhs {
    private String             mLhs;   // lhs is a simple ID
    private List<Expr>         mItems; // subsequent calls upon the ID
    private SetListConstructor mLc;    // not a simple ID, but a list

    public AssignmentLhs(String lhs, List<Expr> items) {
        mLhs   = lhs;
        mItems = items;
        mLc    = null;
    }

    public AssignmentLhs(SetListConstructor lc) {
        mLhs   = null;
        mItems = null;
        mLc    = lc;
    }

    public Expr getExpr() throws UndefinedOperationException {
        if (mLhs != null && mItems != null && mItems.size() <= 0) {
            return new Variable(mLhs);
        } else if (mLhs != null && mItems != null && mItems.size() > 0) {
            Expr call = new Variable(mLhs);
            for (Expr e : mItems) {
                List<Expr> args = new LinkedList<Expr>();
                args.add(e);
                call = new Call(call, args);
            }
            return call;
        } else if (mLhs == null && mItems == null && mLc != null) {
            return mLc;
        } else {
            throw new UndefinedOperationException("Left-hand-side of `" + this + " := ...´ is malformed.");
        }
    }

    public Value setValue(Value v) throws SetlException {
        if (mLhs != null && mItems != null && mItems.size() <= 0) {
            setVariableToValue(v);
        } else if (mLhs != null && mItems != null && mItems.size() > 0) {
            setVariableAfterCallsToValue(v);
        } else if (mLhs == null && mItems == null && mLc != null) {
            setListToVariable(v);
        } else {
            throw new UndefinedOperationException("Left-hand-side of `" + this + " := " + v + "´ is malformed.");
        }
        return v.clone();
    }

    public String toString() {
        String r = "";
        if (mLhs != null) {
            r = mLhs.toString();
        }
        if (mItems != null && mItems.size() > 0) {
            for (Expr e: mItems) {
                r += "(" + e + ")";
            }
        }
        if (mLc != null) {
            r = mLc.toString();
        }
        return r;
    }

    private void setVariableToValue(Value newValue) {
        Environment.putValue(mLhs, newValue.clone());
    }

    // first evaluate the variable, then subsequently perform all calls and assign value to last result
    private void setVariableAfterCallsToValue(Value newValue) throws SetlException {
        Value current = (new Variable(mLhs)).eval();
        for (int i = 0; i < mItems.size(); ++i) {
            if (current instanceof CollectionValue) {
                Value index   = mItems.get(i).eval();
                if (i < mItems.size() - 1) {
                    current = current.getMemberUnCloned(index);
                } else {
                    current.setMember(index, newValue); // no v.clone() here, because setMember() already clones
                }
            } else {
                throw new IncompatibleTypeException("Left-hand-side of `" + this + " := " + newValue + "´ is unusable for list assignment.");
            }
        }
    }

    private void setListToVariable(Value newValue) throws SetlException {
        if (newValue instanceof SetlList) {
            if ( ! mLc.setIds((SetlList) newValue)) {
                throw new IncompatibleTypeException("Right-hand-side of `" + this + " := " + newValue + "´ is unusable for list assignment.");
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " := " + newValue + "´ is unusable for list assignment.");
        }
    }
}

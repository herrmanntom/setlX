package interpreter.expressions;

import interpreter.Environment;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlTuple;
import interpreter.types.Value;

import java.util.List;

public class AssignmentLhs {
    private String              mLhs;
    private List<Expr>          mItems;
    private SetTupleConstructor mTc;

    public AssignmentLhs(String lhs, List<Expr> items) {
        mLhs   = lhs;
        mItems = items;
        mTc    = null;
    }

    public AssignmentLhs(SetTupleConstructor tc) {
        mLhs   = null;
        mItems = null;
        mTc    = tc;
    }

    public Value setValue(Value v) throws SetlException {
        if (mLhs != null && mItems != null && mItems.size() <= 0) {
            Environment.putValue(mLhs, v.clone());
        } else if (mLhs != null && mItems != null && mItems.size() > 0) {
            Value current = (new Variable(mLhs)).eval();
            for (int i = 0; i < mItems.size(); ++i) {
                if (current instanceof CollectionValue) {
                    Value index   = mItems.get(i).eval();
                    if (i < mItems.size() - 1) {
                        current = current.getMemberUnCloned(index);
                    } else {
                        current.setMember(index, v);
                    }
                } else {
                    throw new IncompatibleTypeException("Left-hand-side of `" + this + " := " + v + "´ is unusable for tuple assignment.");
                }
            }
        } else if (mLhs == null && mItems == null && mTc != null) {
            if (v instanceof SetlTuple) {
                if (!mTc.setIds((SetlTuple) v)) {
                    throw new IncompatibleTypeException("Right-hand-side of `" + this + " := " + v + "´ is unusable for tuple assignment.");
                }
            } else {
                throw new IncompatibleTypeException("Right-hand-side of `" + this + " := " + v + "´ is unusable for tuple assignment.");
            }
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
        if (mTc != null) {
            r = mTc.toString();
        }
        return r;
    }
}

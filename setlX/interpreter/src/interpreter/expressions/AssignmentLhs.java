package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.types.CollectionValue;
import interpreter.types.Value;

import java.util.LinkedList;
import java.util.List;

public class AssignmentLhs {
    private Expr        mLhs;   // Lhs (should be either a Variable or ListConstructor)
    private List<Expr>  mItems; // subsequent calls upon the Expr

    public AssignmentLhs(Expr lhs, List<Expr> items) {
        mLhs   = lhs;
        mItems = items;
    }
    
    public AssignmentLhs(Expr lhs) {
        this(lhs, null);
    }

    public Expr getExpr() {
        Expr result = mLhs;
        if (mItems != null && mItems.size() > 0) {
            for (Expr e : mItems) {
                List<Expr> args = new LinkedList<Expr>();
                args.add(e);
                result = new Call(result, args);
            }
        }
        return result;
    }

    public Value setValue(Value v) throws SetlException {
        if (mItems != null && mItems.size() > 0) {
            setVariableAfterCallsToValue(v);
        } else {
            mLhs.assign(v);
        }
        return v;
    }

    public String toString(int tabs) {
        String result = mLhs.toString(tabs);
        if (mItems != null && mItems.size() > 0) {
            for (Expr e: mItems) {
                result += "(" + e.toString(tabs) + ")";
            }
        }
        return result;
    }

    // first evaluate the variable, then subsequently perform all calls and assign value to last result
    private void setVariableAfterCallsToValue(Value newValue) throws SetlException {
        Value current = mLhs.eval();
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
}


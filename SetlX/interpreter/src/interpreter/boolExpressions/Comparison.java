package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;

public class Comparison extends Expr {
    public final static int IN          = 0;
    public final static int NOTIN       = 1;
    public final static int EQUAL       = 2;
    public final static int UNEQUAL     = 3;
    public final static int LESSTHAN    = 4;
    public final static int EQUALORLESS = 5;
    public final static int MORETHAN    = 6;
    public final static int EQUALORMORE = 7;

    private Expr mLhs;
    private int  mType;
    private Expr mRhs;

    public Comparison(Expr lhs, int type, Expr rhs) {
        mLhs  = lhs;
        mType = type;
        mRhs  = rhs;
    }

    public SetlBoolean evaluate() throws SetlException {
        Value lhs           = mLhs.eval();
        Value rhs           = mRhs.eval();

        if (mType == IN) {
            return rhs.containsMember(lhs);
        } else if (mType == NOTIN) {
            return rhs.containsMember(lhs).not();
        } else if (mType == EQUAL) {
            return lhs.isEqual(rhs);
        } else if (mType == UNEQUAL) {
            return lhs.isEqual(rhs).not();
        } else if (mType == LESSTHAN) {
            return lhs.isLessThan(rhs);
        } else if (mType == EQUALORLESS) {
            return lhs.isLessThan(rhs).or(lhs.isEqual(rhs));
        } else if (mType == MORETHAN) {
            return rhs.isLessThan(lhs);
        } else if (mType == EQUALORMORE) {
            return rhs.isLessThan(lhs).or(rhs.isEqual(lhs));
        } else {
            throw new UndefinedOperationException("This comparison type is undefined.");
        }
    }

    public String toString() {
        String result   = mLhs.toString() + " ";
        switch (mType) {
            case IN:
                result += "in";
                break;
            case NOTIN:
                result += "notin";
                break;
            case EQUAL:
                result += "=";
                break;
            case UNEQUAL:
                result += "/=";
                break;
            case LESSTHAN:
                result += "<";
                break;
            case EQUALORLESS:
                result += "<=";
                break;
            case MORETHAN:
                result += ">";
                break;
            case EQUALORMORE:
                result += ">=";
                break;
            default:
                result += "??";
                break;
        }
        return result + " " + mRhs;
    }
}

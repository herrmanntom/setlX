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

    /*
     * Note that these comparisons do not always behave as expected when
     * the compared values are no numbers!
     *
     * For example:
     * Set comparisons are based upon the subset relation
     * a < b
     * is true, when a is a subset of b.
     *
     * However the negation of this comparison
     * !(a < b)
     * is not (in all cases) equal to
     * a >= b
     * because it is possible that a is not a subset of b AND
     * b is neither subset nor equal to a (e.g. a := {1}; b := {2}).
     * In this case
     * !(a < b)
     * would be true, but
     * a >= b
     * would be false.
     *
     * When comparing numbers
     * !(a < b)
     * would always be the same as
     * a >= b
     */

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
            return SetlBoolean.get(lhs.isLessThan(rhs) == SetlBoolean.TRUE || lhs.isEqual(rhs) == SetlBoolean.TRUE);
        } else if (mType == MORETHAN) {
            // note: rhs and lhs swapped!
            return rhs.isLessThan(lhs);
        } else if (mType == EQUALORMORE) {
            // note: rhs and lhs swapped!
            return SetlBoolean.get(rhs.isLessThan(lhs) == SetlBoolean.TRUE || rhs.isEqual(lhs) == SetlBoolean.TRUE);
        } else {
            throw new UndefinedOperationException("This comparison type is undefined.");
        }
    }

    public String toString(int tabs) {
        String result   = mLhs.toString(tabs) + " ";
        switch (mType) {
            case IN:
                result += "in";
                break;
            case NOTIN:
                result += "notin";
                break;
            case EQUAL:
                result += "==";
                break;
            case UNEQUAL:
                result += "!=";
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
        return result + " " + mRhs.toString(tabs);
    }
}


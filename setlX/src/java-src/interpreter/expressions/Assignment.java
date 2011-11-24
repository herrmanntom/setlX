package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Value;

public class Assignment extends Expr {
    public final static int DIRECT      = 0;
    public final static int SUM         = 1;
    public final static int DIFFERENCE  = 2;
    public final static int PRODUCT     = 3;
    public final static int DIVISION    = 4;
    public final static int MODULO      = 5;

    private AssignmentLhs mLhs;
    private int           mType;
    private Expr          mRhs;

    public Assignment(AssignmentLhs lhs, int type, Expr rhs) {
        mLhs  = lhs;
        mType = type;
        mRhs  = rhs;
    }

    public Value evaluate() throws SetlException {
        Expr  rhs = null;
        Expr  lhs = mLhs.getExpr();
        if (mType == DIRECT) {
            rhs = mRhs;
        } else if (mType == SUM) {
            rhs = new Sum(lhs, mRhs);
        } else if (mType == DIFFERENCE) {
            rhs = new Difference(lhs, mRhs);
        } else if (mType == PRODUCT) {
            rhs = new Product(lhs, mRhs);
        } else if (mType == DIVISION) {
            rhs = new Division(lhs, mRhs);
        } else if (mType == MODULO) {
            rhs = new Modulo(lhs, mRhs);
        } else {
            throw new UndefinedOperationException("This assignment type is undefined.");
        }
        return mLhs.setValue(rhs.eval());
    }

    public String toString(int tabs) {
        String result   = mLhs.toString(tabs) + " ";
        switch (mType) {
            case DIRECT:
                result += ":=";
                break;
            case SUM:
                result += "+=";
                break;
            case DIFFERENCE:
                result += "-=";
                break;
            case PRODUCT:
                result += "*=";
                break;
            case DIVISION:
                result += "/=";
                break;
            case MODULO:
                result += "%=";
                break;
            default:
                result += "??";
                break;
        }
        return result + " " + mRhs.toString(tabs);
    }
}


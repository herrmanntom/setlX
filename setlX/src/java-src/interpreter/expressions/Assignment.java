package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
assignment
    : (variable ('(' anyExpr ')')* | idList) (':=' | '+=' | '-=' | '*=' | '/=' | '%=') ((assignment)=> assignment | anyExpr)
    ;

implemented here as:
       ====================================   ====---====---====---====---====---====   ===================================
                    mLhs                                       mType                                mRhs
*/

public class Assignment extends Expr {
    public final static int DIRECT      = 0; // ':='
    public final static int SUM         = 1; // '+='
    public final static int DIFFERENCE  = 2; // '-='
    public final static int PRODUCT     = 3; // '*='
    public final static int DIVISION    = 4; // '/='
    public final static int MODULO      = 5; // '%='

    private AssignmentLhs mLhs;
    private int           mType;
    private Expr          mRhs;
    private Expr          mExecutionRhs; // executed rhs, e.g. mLhs + mRhs, when type == "+="

    public Assignment(AssignmentLhs lhs, int type, Expr rhs) {
        mLhs  = lhs;
        mType = type;
        mRhs  = rhs;
        // build executed rhs expression
        switch (type) {
            case DIRECT:
                mExecutionRhs = rhs;
                break;
            case SUM:
                mExecutionRhs = new Sum       (lhs.getExpr(), rhs);
                break;
            case DIFFERENCE:
                mExecutionRhs = new Difference(lhs.getExpr(), rhs);
                break;
            case PRODUCT:
                mExecutionRhs = new Product   (lhs.getExpr(), rhs);
                break;
            case DIVISION:
                mExecutionRhs = new Division  (lhs.getExpr(), rhs);
                break;
            case MODULO:
                mExecutionRhs = new Modulo    (lhs.getExpr(), rhs);
                break;
            default:
                mExecutionRhs = null;
                break;
        }
    }

    public Value evaluate() throws SetlException {
        if (mExecutionRhs == null) {
            throw new UndefinedOperationException("This assignment type is undefined.");
        }
        return mLhs.setValue(mExecutionRhs.eval());
    }

    /* string operations */

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

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = null;
        switch (mType) {
            case DIRECT:
                result = new Term("'assignment");
                break;
            case SUM:
                result = new Term("'sumAssignment");
                break;
            case DIFFERENCE:
                result = new Term("'differenceAssignment");
                break;
            case PRODUCT:
                result = new Term("'productAssignment");
                break;
            case DIVISION:
                result = new Term("'divisionAssignment");
                break;
            case MODULO:
                result = new Term("'moduloAssignment");
                break;
            default:
                result = new Term("'undefinedAssignment");
                break;
        }
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }
}


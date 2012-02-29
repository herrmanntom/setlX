package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.TermConverter;

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
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER_DIRECT     = "'assignment";           // ':='
    public  final static String FUNCTIONAL_CHARACTER_SUM        = "'sumAssignment";        // '+='
    public  final static String FUNCTIONAL_CHARACTER_DIFFERENCE = "'differenceAssignment"; // '-='
    public  final static String FUNCTIONAL_CHARACTER_PRODUCT    = "'productAssignment";    // '*='
    public  final static String FUNCTIONAL_CHARACTER_DIVISION   = "'divisionAssignment";   // '/='
    public  final static String FUNCTIONAL_CHARACTER_MODULO     = "'moduloAssignment";     // '%='

    public  final static int    DIRECT                          = 0; // ':='
    public  final static int    SUM                             = 1; // '+='
    public  final static int    DIFFERENCE                      = 2; // '-='
    public  final static int    PRODUCT                         = 3; // '*='
    public  final static int    DIVISION                        = 4; // '/='
    public  final static int    MODULO                          = 5; // '%='

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
                mExecutionRhs = new Multiply  (lhs.getExpr(), rhs);
                break;
            case DIVISION:
                mExecutionRhs = new Divide    (lhs.getExpr(), rhs);
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

    public Term toTerm() {
        Term result = null;
        switch (mType) {
            case DIRECT:
                result = new Term(FUNCTIONAL_CHARACTER_DIRECT);
                break;
            case SUM:
                result = new Term(FUNCTIONAL_CHARACTER_SUM);
                break;
            case DIFFERENCE:
                result = new Term(FUNCTIONAL_CHARACTER_DIFFERENCE);
                break;
            case PRODUCT:
                result = new Term(FUNCTIONAL_CHARACTER_PRODUCT);
                break;
            case DIVISION:
                result = new Term(FUNCTIONAL_CHARACTER_DIVISION);
                break;
            case MODULO:
                result = new Term(FUNCTIONAL_CHARACTER_MODULO);
                break;
            default:
                result = new Term("'undefinedAssignment");
                break;
        }
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static Assignment termToExpr(Term term) throws TermConversionException {
        String  fc  = term.functionalCharacter().getUnquotedString();
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + fc);
        } else {
            int type = -1;
            if        (fc.equals(FUNCTIONAL_CHARACTER_DIRECT)    ) {
                type = DIRECT;
            } else if (fc.equals(FUNCTIONAL_CHARACTER_SUM)       ) {
                type = SUM;
            } else if (fc.equals(FUNCTIONAL_CHARACTER_DIFFERENCE)) {
                type = DIFFERENCE;
            } else if (fc.equals(FUNCTIONAL_CHARACTER_PRODUCT)   ) {
                type = PRODUCT;
            } else if (fc.equals(FUNCTIONAL_CHARACTER_DIVISION)  ) {
                type = DIVISION;
            } else if (fc.equals(FUNCTIONAL_CHARACTER_MODULO)    ) {
                type = MODULO;
            } else {
                throw new TermConversionException("malformed " + fc);
            }
            AssignmentLhs   lhs = AssignmentLhs.valueToAssignmentLhs(term.firstMember());
            Expr            rhs = TermConverter.valueToExpr(term.lastMember());
            return new Assignment(lhs, type, rhs);
        }
    }
}


package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
assignment
    : assignable (':=' | '+=' | '-=' | '*=' | '/=' | '%=') ((assignment)=> assignment | anyExpr)
    ;

implemented here as:
      ==========  ====---====---====---====---====---====   ===================================
         mLhs                      mType                                  mRhs
*/

public class Assignment extends Expr {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER_DIRECT     = "^assignment";           // ':='
    public  final static String FUNCTIONAL_CHARACTER_SUM        = "^sumAssignment";        // '+='
    public  final static String FUNCTIONAL_CHARACTER_DIFFERENCE = "^differenceAssignment"; // '-='
    public  final static String FUNCTIONAL_CHARACTER_PRODUCT    = "^productAssignment";    // '*='
    public  final static String FUNCTIONAL_CHARACTER_DIVISION   = "^divisionAssignment";   // '/='
    public  final static String FUNCTIONAL_CHARACTER_MODULO     = "^moduloAssignment";     // '%='

    public  final static int    DIRECT                          = 0; // ':='
    public  final static int    SUM                             = 1; // '+='
    public  final static int    DIFFERENCE                      = 2; // '-='
    public  final static int    PRODUCT                         = 3; // '*='
    public  final static int    DIVISION                        = 4; // '/='
    public  final static int    MODULO                          = 5; // '%='

    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE                      = 1000;

    private Expr    mLhs;
    private int     mType;
    private Expr    mRhs;
    private Expr    mExecutionRhs; // executed rhs, e.g. mLhs + mRhs, when type == "+="

    public Assignment(Expr lhs, int type, Expr rhs) {
        mLhs  = lhs;
        mType = type;
        mRhs  = rhs;
        // build executed rhs expression
        switch (type) {
            case DIRECT:
                mExecutionRhs = rhs;
                break;
            case SUM:
                mExecutionRhs = new Sum       (lhs, rhs);
                break;
            case DIFFERENCE:
                mExecutionRhs = new Difference(lhs, rhs);
                break;
            case PRODUCT:
                mExecutionRhs = new Multiply  (lhs, rhs);
                break;
            case DIVISION:
                mExecutionRhs = new Divide    (lhs, rhs);
                break;
            case MODULO:
                mExecutionRhs = new Modulo    (lhs, rhs);
                break;
            default:
                mExecutionRhs = null;
                break;
        }
    }

    protected Value evaluate() throws SetlException {
        if (mExecutionRhs == null) {
            throw new UndefinedOperationException("This assignment type is undefined.");
        }
        Value assigned = mLhs.assign(mExecutionRhs.eval());

        if (Environment.isTraceAssignments()) {
            Environment.outWriteLn("~< Trace: " + mLhs.toString() + " := " + assigned + " >~");
        }

        return assigned;
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
            Expr lhs = TermConverter.valueToExpr(term.firstMember());
            Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new Assignment(lhs, type, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


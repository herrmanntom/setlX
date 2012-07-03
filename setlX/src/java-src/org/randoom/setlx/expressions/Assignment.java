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
    public  final static String     FUNCTIONAL_CHARACTER_DIRECT     = "^assignment";           // ':='
    public  final static String     FUNCTIONAL_CHARACTER_SUM        = "^sumAssignment";        // '+='
    public  final static String     FUNCTIONAL_CHARACTER_DIFFERENCE = "^differenceAssignment"; // '-='
    public  final static String     FUNCTIONAL_CHARACTER_PRODUCT    = "^productAssignment";    // '*='
    public  final static String     FUNCTIONAL_CHARACTER_DIVISION   = "^divisionAssignment";   // '/='
    public  final static String     FUNCTIONAL_CHARACTER_MODULO     = "^moduloAssignment";     // '%='
    // Trace all assignments. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean    sTraceAssignments               = false;

    // type of assignment
    public  final static int        DIRECT                          = 0; // ':='
    public  final static int        SUM                             = 1; // '+='
    public  final static int        DIFFERENCE                      = 2; // '-='
    public  final static int        PRODUCT                         = 3; // '*='
    public  final static int        DIVISION                        = 4; // '/='
    public  final static int        MODULO                          = 5; // '%='

    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE                      = 1000;

    private final Expr  mLhs;
    private final int   mType;
    private final Expr  mRhs;
    private final Expr  mExecutionRhs; // executed rhs, e.g. mLhs + mRhs, when type == "+="

    public Assignment(final Expr lhs, final int type, final Expr rhs) {
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
        final Value assigned = mLhs.assign(mExecutionRhs.eval());

        if (sTraceAssignments) {
            Environment.outWriteLn("~< Trace: " + mLhs.toString() + " := " + assigned + " >~");
        }

        return assigned;
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mLhs.appendString(sb, tabs);
        sb.append(" ");
        switch (mType) {
            case DIRECT:
                sb.append(":=");
                break;
            case SUM:
                sb.append("+=");
                break;
            case DIFFERENCE:
                sb.append("-=");
                break;
            case PRODUCT:
                sb.append("*=");
                break;
            case DIVISION:
                sb.append("/=");
                break;
            case MODULO:
                sb.append("%=");
                break;
            default:
                sb.append("??");
                break;
        }
        sb.append(" ");
        mRhs.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = null;
        switch (mType) {
            case DIRECT:
                result = new Term(FUNCTIONAL_CHARACTER_DIRECT, 2);
                break;
            case SUM:
                result = new Term(FUNCTIONAL_CHARACTER_SUM, 2);
                break;
            case DIFFERENCE:
                result = new Term(FUNCTIONAL_CHARACTER_DIFFERENCE, 2);
                break;
            case PRODUCT:
                result = new Term(FUNCTIONAL_CHARACTER_PRODUCT, 2);
                break;
            case DIVISION:
                result = new Term(FUNCTIONAL_CHARACTER_DIVISION, 2);
                break;
            case MODULO:
                result = new Term(FUNCTIONAL_CHARACTER_MODULO, 2);
                break;
            default:
                result = new Term("'undefinedAssignment", 2);
                break;
        }
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }

    public static Assignment termToExpr(Term term) throws TermConversionException {
        final String fc = term.functionalCharacter().getUnquotedString();
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
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new Assignment(lhs, type, rhs);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


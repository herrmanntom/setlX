package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
comparison
    : expr ('==' | '!=' | '<' | '<=' | '>' | '>=' | 'in' | 'notin') expr
    ;

implemented here as:
      ====  ====---====---===---====---===---====---====---=======  ====
      mLhs                         mType                            mRhs
*/

public class Comparison extends Expr {
    public final static int EQUAL       = 0; // '=='
    public final static int UNEQUAL     = 1; // '!='
    public final static int LESS        = 2; // '<'
    public final static int LESSorEQUAL = 3; // '<='
    public final static int MORE        = 4; // '>'
    public final static int MOREorEQUAL = 5; // '>='
    public final static int IN          = 6; // 'in'
    public final static int NOTIN       = 7; // 'notin'

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

        switch (mType) {
            case EQUAL:
                return lhs.isEqual(rhs);
            case UNEQUAL:
                return lhs.isEqual(rhs).not();
            case LESS:
                return lhs.isLessThan(rhs);
            case LESSorEQUAL:
                return SetlBoolean.get(lhs.isLessThan(rhs) == SetlBoolean.TRUE || lhs.isEqual(rhs) == SetlBoolean.TRUE);
            case MORE:
                // note: rhs and lhs swapped!
                return rhs.isLessThan(lhs);
            case MOREorEQUAL:
                // note: rhs and lhs swapped!
                return SetlBoolean.get(rhs.isLessThan(lhs) == SetlBoolean.TRUE || rhs.isEqual(lhs) == SetlBoolean.TRUE);
            case IN:
                return rhs.containsMember(lhs);
            case NOTIN:
                return rhs.containsMember(lhs).not();
            default:
                throw new UndefinedOperationException("This comparison type is undefined.");
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result   = mLhs.toString(tabs) + " ";
        switch (mType) {
            case EQUAL:
                result += "==";
                break;
            case UNEQUAL:
                result += "!=";
                break;
            case LESS:
                result += "<";
                break;
            case LESSorEQUAL:
                result += "<=";
                break;
            case MORE:
                result += ">";
                break;
            case MOREorEQUAL:
                result += ">=";
                break;
            case IN:
                result += "in";
                break;
            case NOTIN:
                result += "notin";
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
            case EQUAL:
                result = new Term("'equal");
                break;
            case UNEQUAL:
                result = new Term("'unequal");
                break;
            case LESS:
                result = new Term("'less");
                break;
            case LESSorEQUAL:
                result = new Term("'lessOrEqual");
                break;
            case MORE:
                result = new Term("'more");
                break;
            case MOREorEQUAL:
                result = new Term("'moreOrEqual");
                break;
            case IN:
                result = new Term("'in");
                break;
            case NOTIN:
                result = new Term("'notin");
                break;
            default:
                result = new Term("'undefinedComparison");
                break;
        }
        result.addMember(mLhs.toTerm());
        result.addMember(mRhs.toTerm());
        return result;
    }
}


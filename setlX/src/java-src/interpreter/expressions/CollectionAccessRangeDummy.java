package interpreter.expressions;

import interpreter.types.RangeDummy;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
collectionAccessParams
    : (expr '..')=> expr '..' expr?
    | [...]
    ;

this class implements a range token inside the parameters of a CollectionAccess:
                         ====
*/

public class CollectionAccessRangeDummy extends Expr {
    // functional character used in terms (MUST be classname starting with lower case letter!)
    private final static String                     FUNCTIONAL_CHARACTER = "'collectionAccessRangeDummy";
    // precedence level in SetlX-grammar
    private final static int                        PRECEDENCE           = 9999;

    public  final static CollectionAccessRangeDummy CARD                 = new CollectionAccessRangeDummy();

    private              int                        mLineNr;

    private CollectionAccessRangeDummy() {
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
    }

    public RangeDummy evaluate() {
        return RangeDummy.RD;
    }

    /* string operations */

    public String toString(int tabs) {
        return " .. ";
    }

    /* term operations */

    public Term toTerm() {
        return new Term(FUNCTIONAL_CHARACTER);
    }

    public static CollectionAccessRangeDummy termToExpr(Term term) {
        return CARD;
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


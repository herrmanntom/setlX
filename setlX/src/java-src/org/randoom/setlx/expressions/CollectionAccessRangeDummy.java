package org.randoom.setlx.expressions;

import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.Term;

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
    private final static String                     FUNCTIONAL_CHARACTER = "^collectionAccessRangeDummy";
    // precedence level in SetlX-grammar
    private final static int                        PRECEDENCE           = 9999;

    public  final static CollectionAccessRangeDummy CARD                 = new CollectionAccessRangeDummy();

    private CollectionAccessRangeDummy() {}

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


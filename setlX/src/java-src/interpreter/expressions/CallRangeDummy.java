package interpreter.expressions;

import interpreter.types.RangeDummy;
import interpreter.types.Term;

/*
grammar rule:
callParameters
    : (expr '..')=> expr '..' expr?
    | [...]
    ;

this class implements a range token inside the parameters of a call:
                         ====
*/

public class CallRangeDummy extends Expr {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = "'callRangeDummy";

    public final static CallRangeDummy CRD = new CallRangeDummy();

    private CallRangeDummy() { }

    public RangeDummy evaluate() {
        return RangeDummy.RD;
    }

    /* string operations */

    public String toString(int tabs) {
        return "..";
    }

    /* term operations */

    public Term toTerm() {
        return new Term(FUNCTIONAL_CHARACTER);
    }
}


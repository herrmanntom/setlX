package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
prefixOperation
    : [...]
    | '-' factor
    ;

implemented here as:
          ======
          mExpr
*/

public class Negate extends Expr {
    private Expr mExpr;

    public Negate(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().negate();
    }

    /* String operations */

    public String toString(int tabs) {
        return "-" + mExpr.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'negate");
        result.addMember(mExpr.toTerm());
        return result;
    }
}


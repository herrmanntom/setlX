package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlInt;
import interpreter.types.Term;

/*
grammar rule:
prefixOperation
    : [...]
    | '#' factor
    ;

implemented here as:
          ======
          mExpr
*/

public class Cardinality extends Expr {
    private Expr mExpr;

    public Cardinality(Expr expr) {
        mExpr = expr;
    }

    public SetlInt evaluate() throws SetlException {
        return mExpr.eval().cardinality();
    }

    /* string operations */

    public String toString(int tabs) {
        return "#" + mExpr.toString(tabs);
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'cardinality");
        result.addMember(mExpr.toTerm());
        return result;
    }
}


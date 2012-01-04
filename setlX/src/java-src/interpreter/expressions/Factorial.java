package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlInt;
import interpreter.types.Term;

/*
grammar rule:
factor
    : [...]
    | simpleFactor '!'?
    ;

implemented here as:
      ============
         mExpr
*/

public class Factorial extends Expr {
    private Expr mExpr;

    public Factorial(Expr expr) {
        mExpr = expr;
    }

    public SetlInt evaluate() throws SetlException {
        return mExpr.eval().factorial();
    }

    /* string operations */

    public String toString(int tabs) {
        return mExpr.toString(tabs) + "!";
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'factorial");
        result.addMember(mExpr.toTerm());
        return result;
    }
}


package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
simpleFactor
    : '(' expr ')'
    | [...]
    ;

implemented here as:
          ====
          mExpr
*/

public class BracketedExpr extends Expr {
    private Expr mExpr;

    public BracketedExpr(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval();
    }

    /* string operations */

    public String toString(int tabs) {
        return "(" + mExpr.toString(tabs) + ")";
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'brackets");
        result.addMember(mExpr.toTerm());
        return result;
    }
}


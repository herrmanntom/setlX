package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
prefixOperation
    : '+/' factor
    | [...]
    ;

implemented here as:
           ======
           mExpr
*/

public class AddMembers extends Expr {
    private Expr mExpr;

    public AddMembers(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().addMembers();
    }

    /* string operations */

    public String toString(int tabs) {
        return "+/" + mExpr.toString(tabs);
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'addMembers");
        result.addMember(mExpr.toTerm());
        return result;
    }
}


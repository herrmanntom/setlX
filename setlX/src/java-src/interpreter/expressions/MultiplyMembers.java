package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

//  grammar rule:
//  prefixOperation
//      : '*/' factor
//      | [...]
//      ;
//
//  implemented here as:
//             ======
//             mExpr

public class MultiplyMembers extends Expr {
    private Expr mExpr;

    public MultiplyMembers(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().multiplyMembers();
    }

    /* string operations */

    public String toString(int tabs) {
        return "*/" + mExpr.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'multiplyMembers");
        result.addMember(mExpr.toTerm());
        return result;
    }
}


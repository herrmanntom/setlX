package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class From extends Expr {
    private Expr mExpr;

    public From(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        Value collection = mExpr.eval();
        Value element    = collection.arbitraryMember();
        if (!(element instanceof SetlOm)) {
            collection.removeMember(element);
        }
        return element;
    }

    public String toString() {
        return "from " + mExpr;
    }
}

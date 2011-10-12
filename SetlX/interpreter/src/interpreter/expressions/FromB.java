package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class FromB extends Expr {
    private Expr mExpr;

    public FromB(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        Value collection = mExpr.eval();
        Value element    = collection.firstMember();
        if (!(element instanceof SetlOm)) {
            collection.removeFirstMember();
        }
        return element;
    }

    public String toString() {
        return "fromb " + mExpr;
    }
}

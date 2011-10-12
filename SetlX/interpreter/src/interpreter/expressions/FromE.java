package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class FromE extends Expr {
    private Expr mExpr;

    public FromE(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        Value collection = mExpr.eval();
        Value element    = collection.lastMember();
        if (!(element instanceof SetlOm)) {
            collection.removeLastMember();
        }
        return element;
    }

    public String toString() {
        return "frome " + mExpr;
    }
}

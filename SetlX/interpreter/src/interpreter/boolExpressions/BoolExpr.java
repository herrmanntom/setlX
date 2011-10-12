package interpreter.boolExpressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;

public class BoolExpr extends Expr {
    private Expr mExpr;

    public BoolExpr(Expr expr) {
        mExpr = expr;
    }

    public SetlBoolean eval() throws SetlException {
        Value v = mExpr.eval();
        if (v instanceof SetlBoolean) {
            return (SetlBoolean) v;
        } else {
            throw new IncompatibleTypeException("`" + v + "´ is not a Boolean value.");
        }
    }

    public Value evaluate() {
        return eval();
    }

    public boolean evalToBool() throws IncompatibleTypeException {
        return (this.eval() == SetlBoolean.TRUE);
    }

    public String toString() {
        return mExpr.toString();
    }
}

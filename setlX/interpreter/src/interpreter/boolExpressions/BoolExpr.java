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
        if (v == SetlBoolean.TRUE || v == SetlBoolean.FALSE) {
            return (SetlBoolean) v;
        } else {
            throw new IncompatibleTypeException("'" + v + "' is not a Boolean value.");
        }
    }

    public Value evaluate() throws SetlException {
        return eval();
    }

    public boolean evalToBool() throws SetlException {
        return (this.eval() == SetlBoolean.TRUE);
    }

    public String toString(int tabs) {
        return mExpr.toString(tabs);
    }
}


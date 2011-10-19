package interpreter.expressions;

import interpreter.types.Value;

public class ValueExpr extends Expr {
    private Value mValue;

    public ValueExpr(Value value) {
        mValue = value;
    }

    public Value eval() {
        return mValue;
    }

    public Value evaluate() {
        return eval();
    }

    public String toString(int tabs) {
        return mValue.toString(tabs);
    }
}



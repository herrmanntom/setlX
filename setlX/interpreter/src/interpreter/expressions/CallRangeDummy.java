package interpreter.expressions;

import interpreter.types.RangeDummy;
import interpreter.types.Value;

public class CallRangeDummy extends Expr {

    public final static CallRangeDummy CRD = new CallRangeDummy();

    private CallRangeDummy(){}

    public Value evaluate() {
        return RangeDummy.RD;
    }

    public String toString(int tabs) {
        return "..";
    }
}


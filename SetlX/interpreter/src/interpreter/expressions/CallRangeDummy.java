package interpreter.expressions;

import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Value;

public class CallRangeDummy extends Expr {

    public final static CallRangeDummy CRD = new CallRangeDummy();

    private CallRangeDummy(){}

    public Value evaluate() throws UndefinedOperationException {
        throw new UndefinedOperationException("dummy called");
    }

    public String toString() {
        return "..";
    }
}


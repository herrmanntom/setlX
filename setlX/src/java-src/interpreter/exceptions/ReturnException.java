package interpreter.exceptions;

import interpreter.types.Value;

public class ReturnException extends NonCatchableInSetlXException {
    private Value mResult;

    public ReturnException(Value result) {
        super("Return");
        mResult = result;
    }

    public Value getValue() {
        return mResult;
    }
}


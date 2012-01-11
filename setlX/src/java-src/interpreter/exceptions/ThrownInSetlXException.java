package interpreter.exceptions;

import interpreter.types.Value;

public class ThrownInSetlXException extends CatchableInSetlXException {
    private Value mValue;

    public ThrownInSetlXException(Value value) {
        super("Error: " + value.toString());
        mValue = value;
    }

    public Value getValue() {
        return mValue;
    }
}


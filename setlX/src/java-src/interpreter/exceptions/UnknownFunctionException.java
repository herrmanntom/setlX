package interpreter.exceptions;

public class UnknownFunctionException extends CatchDuringParsingException {
    public UnknownFunctionException(String msg) {
        super(msg);
    }
}


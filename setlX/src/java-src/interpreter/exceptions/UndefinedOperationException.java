package interpreter.exceptions;

public class UndefinedOperationException extends CatchDuringParsingException {
    public UndefinedOperationException(String msg) {
        super(msg);
    }
}


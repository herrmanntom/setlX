package interpreter.exceptions;

public class IncompatibleTypeException extends CatchDuringParsingException {
    public IncompatibleTypeException(String msg) {
        super(msg);
    }
}


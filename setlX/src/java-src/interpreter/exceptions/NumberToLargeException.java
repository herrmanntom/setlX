package interpreter.exceptions;

public class NumberToLargeException extends CatchDuringParsingException {
    public NumberToLargeException(String msg) {
        super(msg);
    }
}


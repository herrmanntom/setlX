package interpreter.exceptions;

public class IncorrectNumberOfParametersException extends CatchDuringParsingException {
    public IncorrectNumberOfParametersException(String msg) {
        super(msg);
    }
}


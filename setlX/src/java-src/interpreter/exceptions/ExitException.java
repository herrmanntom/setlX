package interpreter.exceptions;

public class ExitException extends NonCatchableInSetlXException {
    public ExitException(String msg) {
        super(msg);
    }
}


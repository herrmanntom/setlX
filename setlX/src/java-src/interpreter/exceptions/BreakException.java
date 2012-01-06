package interpreter.exceptions;

public class BreakException extends NonCatchableInSetlXException {
    public BreakException(String msg) {
        super(msg);
    }
}


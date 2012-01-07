package interpreter.exceptions;

public abstract class ParserException extends CatchableInSetlXException {
    public ParserException(String msg) {
        super(msg);
    }
}


package interpreter.exceptions;

public abstract class ParserException extends CatchDuringParsingException {
    public ParserException(String msg) {
        super(msg);
    }
}


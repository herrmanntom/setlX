package interpreter.exceptions;

public class SyntaxErrorException extends ParserException {

    public SyntaxErrorException(String msg) {
        super(msg);
    }
}

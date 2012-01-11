package interpreter.exceptions;

/*
 * Exceptions inheriting from this class are supposed to be catched during
 * parsing and later executing a SetlX expression or block.
 */

public abstract class CatchDuringParsingException extends CatchableInSetlXException {
    public CatchDuringParsingException(String msg) {
        super(msg);
    }
}


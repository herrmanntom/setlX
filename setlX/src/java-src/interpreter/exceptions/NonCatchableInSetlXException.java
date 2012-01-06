package interpreter.exceptions;

/*
 * Exceptions inhereting from this class are not supposed to be catched by
 * SetlX's own try-catch block.
 */

public abstract class NonCatchableInSetlXException extends SetlException {
    public NonCatchableInSetlXException(String msg) {
        super(msg);
    }
}


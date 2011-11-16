package interpreter.expressions;

import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Value;

public class IdListIgnoreDummy extends Expr {

    public final static IdListIgnoreDummy ILID = new IdListIgnoreDummy();

    private IdListIgnoreDummy() {}

    public Value evaluate() throws UndefinedOperationException {
        throw new UndefinedOperationException("dummy called");
    }

    public String toString(int tabs) {
        return "_";
    }
}


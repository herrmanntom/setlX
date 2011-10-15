package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.BreakException;

public class Break extends Statement {

    public Break() { }

    public void execute() throws BreakException {
        throw new BreakException("break");
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "break;";
    }
}

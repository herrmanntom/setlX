package interpreter.statements;

import interpreter.exceptions.ContinueException;
import interpreter.utilities.Environment;

public class Continue extends Statement {

    public Continue() {  }

    public void execute() throws ContinueException {
        throw new ContinueException("continue");
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "continue;";
    }
}

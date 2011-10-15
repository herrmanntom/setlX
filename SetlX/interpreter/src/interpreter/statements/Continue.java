package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.ContinueException;

public class Continue extends Statement {

    public Continue() {  }

    public void execute() throws ContinueException {
        throw new ContinueException("continue");
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "continue;";
    }
}

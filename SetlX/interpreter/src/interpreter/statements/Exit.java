package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.ExitException;

public class Exit extends Statement {

    public Exit() {  }

    public void execute() throws ExitException {
        throw new ExitException("Good Bye! (exit)");
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "exit;";
    }
}

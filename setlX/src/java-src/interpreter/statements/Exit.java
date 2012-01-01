package interpreter.statements;

import interpreter.exceptions.ExitException;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'exit' ';'
    ;
*/

public class Exit extends Statement {

    public Exit() { }

    public void execute() throws ExitException {
        throw new ExitException("Good Bye! (exit)");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "exit;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'exit");
        return result;
    }
}


package interpreter.statements;

import interpreter.exceptions.ContinueException;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'continue' ';'
    ;
*/

public class Continue extends Statement {

    public Continue() { }

    public void execute() throws ContinueException {
        throw new ContinueException("continue");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "continue;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'continue");
        return result;
    }
}


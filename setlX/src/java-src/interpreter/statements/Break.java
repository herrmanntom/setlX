package interpreter.statements;

import interpreter.exceptions.BreakException;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'break' ';'
    ;
*/

public class Break extends Statement {

    public Break() { }

    public void execute() throws BreakException {
        throw new BreakException("break");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "break;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'break");
        return result;
    }
}


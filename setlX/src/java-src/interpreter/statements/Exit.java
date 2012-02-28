package interpreter.statements;

import interpreter.exceptions.ExitException;
import interpreter.exceptions.TermConversionException;
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
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER    = "'exit";

    public  final static Exit   E                       = new Exit();

    private Exit() { }

    public void execute() throws ExitException {
        throw new ExitException("Good Bye! (exit)");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "exit;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        return result;
    }

    public static Exit termToStatement(Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return E;
        }
    }
}


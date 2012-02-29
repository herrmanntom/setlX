package interpreter.statements;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.TermConversionException;
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
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER    = "'break";

    public  final static Break  B                       = new Break();

    private Break() { }

    public void execute() throws BreakException {
        throw new BreakException("break");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "break;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        return result;
    }

    public static Break termToStatement(Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return B;
        }
    }
}


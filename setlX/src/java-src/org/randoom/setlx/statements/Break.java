package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BreakException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'break' ';'
    ;
*/

public class Break extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER    = "^break";

    public  final static Break  B                       = new Break();

    private Break() { }

    protected Value exec() throws BreakException {
        throw new BreakException("break");
    }

    /* string operations */

    public String toString(final int tabs) {
        return Environment.getLineStart(tabs) + "break;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER, 0);
        return result;
    }

    public static Break termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return B;
        }
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BacktrackException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'backtrack' ';'
    ;
*/

public class Backtrack extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String    FUNCTIONAL_CHARACTER = "^backtrack";

    public  final static Backtrack BT                   = new Backtrack();

    private Backtrack() { }

    protected Value exec() throws BacktrackException {
        throw new BacktrackException("Backtrack-statement was executed outside of check-statement.");
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("backtrack;");
    }

    /* term operations */

    public Term toTerm() {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static Backtrack termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return BT;
        }
    }
}


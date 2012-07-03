package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.ContinueException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'continue' ';'
    ;
*/

public class Continue extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER    = "^continue";

    public  final static Continue   C                       = new Continue();

    private Continue() {  }

    protected Value exec() throws ContinueException {
        throw new ContinueException("continue");
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("continue;");
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER, 0);
        return result;
    }

    public static Continue termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return C;
        }
    }
}


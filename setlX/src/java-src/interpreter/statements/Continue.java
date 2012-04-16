package interpreter.statements;

import interpreter.exceptions.ContinueException;
import interpreter.exceptions.TermConversionException;
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
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER    = "^continue";

    public  final static Continue   C                       = new Continue();
    private              int        mLineNr;

    private Continue() {
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = ++Environment.sourceLine;
    }

    public void exec() throws ContinueException {
        throw new ContinueException("continue");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getLineStart(getLineNr(), tabs) + "continue;";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        return result;
    }

    public static Continue termToStatement(Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return C;
        }
    }
}


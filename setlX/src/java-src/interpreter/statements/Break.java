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
    private final static String FUNCTIONAL_CHARACTER    = "^break";

    public  final static Break  B                       = new Break();

    private              int    mLineNr;

    private Break() {
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


    public void exec() throws BreakException {
        throw new BreakException("break");
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getLineStart(getLineNr(), tabs) + "break;";
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


package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                                                                =====
                                                             mStatements
*/

public class SwitchDefaultBranch extends SwitchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "'switchDefaultBranch";

    private Block   mStatements;
    private int     mLineNr;

    public SwitchDefaultBranch(Block statements) {
        mStatements = statements;
        mLineNr     = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = ++Environment.sourceLine;
        mStatements.computeLineNr();
        // block counts an pending line
        --Environment.sourceLine;
    }

    public boolean evalConditionToBool() {
        return true;
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(getLineNr(), tabs);
        result += "default:" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static SwitchDefaultBranch termToBranch(Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Block block = TermConverter.valueToBlock(term.firstMember());
            return new SwitchDefaultBranch(block);
        }
    }
}


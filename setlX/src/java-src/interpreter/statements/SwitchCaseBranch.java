package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                           =========     =====
                           mCondition mStatements
*/

public class SwitchCaseBranch extends SwitchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "'switchCaseBranch";

    private Condition mCondition;
    private Block     mStatements;
    private int       mLineNr;

    public SwitchCaseBranch(Condition condition, Block statements){
        mCondition  = condition;
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
        mCondition.computeLineNr();
        mStatements.computeLineNr();
        // block counts an pending line
        --Environment.sourceLine;
    }

    public boolean evalConditionToBool() throws SetlException {
        return mCondition.evalToBool();
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(getLineNr(), tabs);
        result += "case " + mCondition.toString(tabs) + ":" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static SwitchCaseBranch termToBranch(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Condition   condition   = TermConverter.valueToCondition(term.firstMember());
            Block       block       = TermConverter.valueToBlock(term.lastMember());
            return new SwitchCaseBranch(condition, block);
        }
    }
}


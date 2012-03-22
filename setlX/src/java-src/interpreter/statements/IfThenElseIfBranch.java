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
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    ;

implemented here as:
                                                            =========         =====
                                                            mCondition     mStatements
*/

public class IfThenElseIfBranch extends IfThenAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "'ifThenElseIfBranch";

    private Condition mCondition;
    private Block     mStatements;
    private int       mLineNr;

    public IfThenElseIfBranch(Condition condition, Block statements){
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
        mLineNr = Environment.sourceLine;
        mCondition.computeLineNr();
        mStatements.computeLineNr();
    }

    public boolean evalConditionToBool() throws SetlException {
        return mCondition.evalToBool();
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = " else if (";
        result += mCondition.toString(tabs);
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static IfThenElseIfBranch termToBranch(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Condition   condition   = TermConverter.valueToCondition(term.firstMember());
            Block       block       = TermConverter.valueToBlock(term.lastMember());
            return new IfThenElseIfBranch(condition, block);
        }
    }
}


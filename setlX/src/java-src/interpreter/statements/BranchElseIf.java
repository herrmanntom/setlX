package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.utilities.Condition;

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

public class BranchElseIf extends BranchAbstract {
    private Condition mCondition;
    private Block     mStatements;

    public BranchElseIf(Condition condition, Block statements){
        mCondition  = condition;
        mStatements = statements;
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

    public Term toTerm() throws SetlException {
        Term result = new Term("'if");
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }
}


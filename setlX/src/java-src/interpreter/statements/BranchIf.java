package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;

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

public class BranchIf extends BranchAbstract {
    private Condition mCondition;
    private Block     mStatements;

    public BranchIf(Condition condition, Block statements){
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
        String result = Environment.getTabs(tabs);
        result += "if (";
        result += mCondition.toString(tabs);
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'if");
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }
}


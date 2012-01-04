package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;

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

public class BranchCase extends BranchAbstract {
    private Condition mCondition;
    private Block     mStatements;

    public BranchCase(Condition condition, Block statements){
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
        result += "case " + mCondition.toString(tabs) + ":" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'case");
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }
}


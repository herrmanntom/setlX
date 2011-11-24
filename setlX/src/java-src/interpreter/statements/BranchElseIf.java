package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Condition;

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

    public String toString(int tabs) {
        String result = " else if (";
        result += mCondition.toString(tabs);
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}


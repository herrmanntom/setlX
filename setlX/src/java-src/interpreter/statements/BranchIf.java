package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;

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

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "if (";
        result += mCondition.toString(tabs);
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}


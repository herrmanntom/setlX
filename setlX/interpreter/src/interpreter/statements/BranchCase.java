package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;

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

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "case " + mCondition.toString(tabs) + ":" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }
}


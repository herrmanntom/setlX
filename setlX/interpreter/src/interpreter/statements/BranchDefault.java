package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Environment;

public class BranchDefault extends BranchAbstract {
    private Block       mStatements;

    public BranchDefault(Block statements) {
        mStatements = statements;
    }

    public boolean evalConditionToBool() {
        return true;
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "default:" + Environment.getEndl();
        result += mStatements.toString(tabs + 1);
        return result;
    }
}

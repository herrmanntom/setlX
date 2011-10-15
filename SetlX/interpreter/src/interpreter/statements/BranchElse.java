package interpreter.statements;

import interpreter.exceptions.SetlException;

public class BranchElse extends BranchAbstract {
    private Block       mStatements;

    public BranchElse(Block statements){
        mStatements = statements;
    }

    public boolean evalConditionToBool() {
        return true;
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    public String toString(int tabs) {
        String result = " else ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}

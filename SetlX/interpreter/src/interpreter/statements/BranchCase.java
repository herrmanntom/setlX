package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.SetlException;

public class BranchCase extends BranchAbstract {
    private BoolExpr    mBoolExpr;
    private Block       mStatements;

    public BranchCase(BoolExpr boolExpr, Block statements){
        mBoolExpr   = boolExpr;
        mStatements = statements;
    }

    public boolean evalConditionToBool() throws SetlException {
        return mBoolExpr.evalToBool();
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "case " + mBoolExpr + ":" + Environment.getEndl();
        result += mStatements.toString(tabs + 1);
        return result;
    }
}

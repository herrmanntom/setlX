package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.SetlException;

public class BranchIf extends BranchAbstract {
    private BoolExpr    mBoolExpr;
    private Block       mStatements;

    public BranchIf(BoolExpr boolExpr, Block statements){
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
        result += "if (";
        result += mBoolExpr;
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}

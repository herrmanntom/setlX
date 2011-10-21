package interpreter.statements;

import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.SetlException;

public class BranchElseIf extends BranchAbstract {
    private BoolExpr    mBoolExpr;
    private Block       mStatements;

    public BranchElseIf(BoolExpr boolExpr, Block statements){
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
        String result = " else if (";
        result += mBoolExpr;
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}

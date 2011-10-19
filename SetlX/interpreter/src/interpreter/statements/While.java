package interpreter.statements;

import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.BreakException;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.SetlException;
import interpreter.utilities.Environment;

public class While extends Statement {
    private BoolExpr    mCondition;
    private Block       mStatements;

    public While(BoolExpr condition, Block statements) {
        mCondition  = condition;
        mStatements = statements;
    }

    public void execute() throws SetlException {
        while (mCondition.evalToBool()) {
            try{
                mStatements.execute();
            } catch (ContinueException e) {
                continue;
            } catch (BreakException e) {
                break;
            }
        }
    }
    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "while (" + mCondition + ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}


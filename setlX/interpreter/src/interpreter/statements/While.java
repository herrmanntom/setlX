package interpreter.statements;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.SetlException;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;

public class While extends Statement {
    private Condition mCondition;
    private Block     mStatements;

    public While(Condition condition, Block statements) {
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
        result += "while (" + mCondition.toString(tabs) + ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }
}


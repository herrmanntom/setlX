package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;

import java.util.List;

public class Until extends Statement {
    private BoolExpr        mCond;
    private List<Statement> mStmntList;

    public Until(BoolExpr cond, List<Statement> stmntList) {
        mCond      = cond;
        mStmntList = stmntList;
    }

    public void execute() throws SetlException {
        do {
            try{
                for (Statement stmnt: mStmntList) {
                    stmnt.execute();
                }
            } catch (ContinueException e) {
                continue;
            } catch (ExitException e) {
                break;
            }
        } while (!mCond.evalToBool());
    }

    public String toString(int tabs) {
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "until " + mCond + " loop" + endl;
        for (Statement stmnt: mStmntList) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        result += Environment.getTabs(tabs) + "end loop;";
        return result;
    }
}

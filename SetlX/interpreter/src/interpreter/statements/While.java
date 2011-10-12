package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;

import java.util.List;

public class While extends Statement {
    private BoolExpr        mCond;
    private List<Statement> mStmntList;

    public While(BoolExpr cond, List<Statement> stmntList) {
        mCond      = cond;
        mStmntList = stmntList;
    }

    public void execute() throws SetlException {
        while (mCond.evalToBool()) {
            try{
                for (Statement stmnt: mStmntList) {
                    stmnt.execute();
                }
            } catch (ContinueException e) {
                continue;
            } catch (ExitException e) {
                break;
            }
        }
    }
    public String toString(int tabs) {
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "while " + mCond + " loop" + endl;
        for (Statement stmnt: mStmntList) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        result += Environment.getTabs(tabs) + "end loop;";
        return result;
    }
}

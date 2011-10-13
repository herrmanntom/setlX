package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.SetlException;

import java.util.List;

public class IfThen extends Statement {
    private List<AbstractBranch> mBranchList;

    public IfThen(List<AbstractBranch> branchList) {
        mBranchList = branchList;
    }

    public void execute() throws SetlException {
        for (AbstractBranch b : mBranchList) {
            if (b.getCondition().evalToBool()) {
                for (Statement stmnt: b.getStatements()) {
                    stmnt.execute();
                }
                break;
            }
        }
    }

    public String toString(int tabs) {
        String result = "";
        for (AbstractBranch b : mBranchList) {
            result += b.toString(tabs);
        }
        result += Environment.getTabs(tabs) + "end if;";
        return result;
    }
}

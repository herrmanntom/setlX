package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Environment;

import java.util.List;

public class Switch extends Statement {
    private List<BranchAbstract> mBranchList;

    public Switch(List<BranchAbstract> branchList) {
        mBranchList = branchList;
    }

    public void execute() throws SetlException {
        for (BranchAbstract b : mBranchList) {
            if (b.evalConditionToBool()) {
                b.execute();
                break;
            }
        }
    }

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "switch {" + Environment.getEndl();
        for (BranchAbstract b : mBranchList) {
            result += b.toString(tabs + 1);
        }
        result += Environment.getTabs(tabs) + "}";
        return result;
    }
}

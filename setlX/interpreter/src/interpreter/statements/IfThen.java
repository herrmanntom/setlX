package interpreter.statements;

import interpreter.exceptions.SetlException;

import java.util.List;

public class IfThen extends Statement {
    private List<BranchAbstract> mBranchList;

    public IfThen(List<BranchAbstract> branchList) {
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
        String result = "";
        for (BranchAbstract b : mBranchList) {
            result += b.toString(tabs);
        }
        return result;
    }
}

package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;

import java.util.List;

public class Case extends Statement {
    private List<AbstractBranch> mBranchList;

    public Case(List<AbstractBranch> branchList) {
        mBranchList = branchList;
    }

    public void execute() throws SetlException {
        for( int i = 0; i < mBranchList.size(); i++){
            if(mBranchList.get(i).getCondition().evalToBool()) {
                AbstractBranch  ab        = mBranchList.get(i);
                List<Statement> stmntList = ab.getStatements();
                for(Statement s : stmntList){
                    s.execute();
                }
                break;
            }
        }
    }

    public void addCaseBranch(AbstractBranch b){
        mBranchList.add(b);
    }

    public String toString(int tabs) {
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "case" + endl;
        for (AbstractBranch branch: mBranchList) {
            result += branch.toString(tabs + 1) + endl;
        }
        result += Environment.getTabs(tabs) + "end case;";
        return result;
    }
}

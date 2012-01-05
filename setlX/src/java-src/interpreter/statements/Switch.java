package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.utilities.Environment;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    ;

implemented with different classes which inherit from BranchAbstract:
                    ==========================    ===================
                            BranchCase               BranchDefault
*/

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

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "switch {" + Environment.getEndl();
        for (BranchAbstract b : mBranchList) {
            result += b.toString(tabs + 1);
        }
        result += Environment.getTabs(tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'switchBlock");

        SetlList branchList = new SetlList();
        for (BranchAbstract br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }
}


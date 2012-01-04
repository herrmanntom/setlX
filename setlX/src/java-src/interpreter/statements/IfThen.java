package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlList;
import interpreter.types.Term;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    ;

implemented with different classes which inherit from BranchAbstract:
      ====================================  ===========================================    ====================
                    BranchIf                               BranchElseIf                         BranchElse
*/

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

    /* string operations */

    public String toString(int tabs) {
        String result = "";
        for (BranchAbstract b : mBranchList) {
            result += b.toString(tabs);
        }
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'ifBlock");

        SetlList branchList = new SetlList();
        for (BranchAbstract br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }
}


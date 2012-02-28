package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    ;

implemented with different classes which inherit from BranchAbstract:
                    ==========================    ===================
                         SwitchCaseBranch         SwitchDefaultBranch
*/

public class Switch extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "'switch";

    private List<SwitchAbstractBranch> mBranchList;

    public Switch(List<SwitchAbstractBranch> branchList) {
        mBranchList = branchList;
    }

    public void execute() throws SetlException {
        for (SwitchAbstractBranch b : mBranchList) {
            if (b.evalConditionToBool()) {
                b.execute();
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "switch {" + Environment.getEndl();
        for (SwitchAbstractBranch b : mBranchList) {
            result += b.toString(tabs + 1);
        }
        result += Environment.getTabs(tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        SetlList branchList = new SetlList();
        for (SwitchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static Switch termToStatement(Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            SetlList                    branches    = (SetlList) term.firstMember();
            List<SwitchAbstractBranch>  branchList  = new ArrayList<SwitchAbstractBranch>(branches.size());
            for (Value v : branches) {
                branchList.add(SwitchAbstractBranch.valueToSwitchAbstractBranch(v));
            }
            return new Switch(branchList);
        }
    }
}


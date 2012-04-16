package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    ;

implemented with different classes which inherit from BranchAbstract:
      ====================================  ===========================================    ====================
                  IfThenBranch                          IfThenElseIfBranch                   IfThenElseBranch
*/

public class IfThen extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^ifThen";

    private List<IfThenAbstractBranch> mBranchList;

    public IfThen(List<IfThenAbstractBranch> branchList) {
        mBranchList = branchList;
    }

    public void exec() throws SetlException {
        for (IfThenAbstractBranch br : mBranchList) {
            if (br.evalConditionToBool()) {
                br.execute();
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = "";
        for (IfThenAbstractBranch br : mBranchList) {
            result += br.toString(tabs);
        }
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        SetlList branchList = new SetlList();
        for (IfThenAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static IfThen termToStatement(Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            SetlList                    branches    = (SetlList) term.firstMember();
            List<IfThenAbstractBranch>  branchList  = new ArrayList<IfThenAbstractBranch>(branches.size());
            for (Value v : branches) {
                branchList.add(IfThenAbstractBranch.valueToIfThenAbstractBranch(v));
            }
            return new IfThen(branchList);
        }
    }
}


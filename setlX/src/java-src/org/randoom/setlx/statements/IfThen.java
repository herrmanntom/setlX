package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

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

    private final List<IfThenAbstractBranch> mBranchList;

    public IfThen(final List<IfThenAbstractBranch> branchList) {
        mBranchList = branchList;
    }

    protected Value exec() throws SetlException {
        for (final IfThenAbstractBranch br : mBranchList) {
            if (br.evalConditionToBool()) {
                return br.execute();
            }
        }
        return null;
    }

    /* string operations */

    public String toString(final int tabs) {
        String result = "";
        for (final IfThenAbstractBranch br : mBranchList) {
            result += br.toString(tabs);
        }
        return result;
    }

    /* term operations */

    public Term toTerm() {
        final Term     result     = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList branchList = new SetlList(mBranchList.size());
        for (final IfThenAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static IfThen termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList                   branches   = (SetlList) term.firstMember();
            final List<IfThenAbstractBranch> branchList = new ArrayList<IfThenAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(IfThenAbstractBranch.valueToIfThenAbstractBranch(v));
            }
            return new IfThen(branchList);
        }
    }
}


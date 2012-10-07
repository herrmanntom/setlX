package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
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

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        // binding inside an if-then-else are only valid if present in all branches
        // and last branch is an else-branch
        final int      preBound  = boundVariables.size();
        List<Variable> boundHere = null;
        for (final IfThenAbstractBranch br : mBranchList) {
            final List<Variable> boundTmp = new ArrayList<Variable>(boundVariables);

            br.collectVariablesAndOptimize(boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<Variable>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (mBranchList.get(mBranchList.size() - 1) instanceof IfThenElseBranch) {
            boundVariables.addAll(boundHere);
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        for (final IfThenAbstractBranch br : mBranchList) {
            br.appendString(sb, tabs);
        }
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


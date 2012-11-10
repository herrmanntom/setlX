package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

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
    private final static String FUNCTIONAL_CHARACTER = "^switch";

    private final List<SwitchAbstractBranch> mBranchList;

    public Switch(final List<SwitchAbstractBranch> branchList) {
        mBranchList = branchList;
    }

    protected Value exec(final State state) throws SetlException {
        for (final SwitchAbstractBranch br : mBranchList) {
            if (br.evalConditionToBool(state)) {
                return br.execute(state);
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
        // binding inside an switch are only valid if present in all branches
        // and last branch is a default-branch
        final int      preBound  = boundVariables.size();
        List<Variable> boundHere = null;
        for (final SwitchAbstractBranch br : mBranchList) {
            final List<Variable> boundTmp = new ArrayList<Variable>(boundVariables);

            br.collectVariablesAndOptimize(boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<Variable>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (mBranchList.get(mBranchList.size() - 1) instanceof SwitchDefaultBranch) {
            boundVariables.addAll(boundHere);
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("switch {");
        sb.append(Environment.getEndl());
        for (final SwitchAbstractBranch br : mBranchList) {
            br.appendString(sb, tabs + 1);
        }
        Environment.getLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList branchList = new SetlList(mBranchList.size());
        for (final SwitchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm(state));
        }
        result.addMember(branchList);

        return result;
    }

    public static Switch termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList                    branches    = (SetlList) term.firstMember();
            final List<SwitchAbstractBranch>  branchList  = new ArrayList<SwitchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(SwitchAbstractBranch.valueToSwitchAbstractBranch(v));
            }
            return new Switch(branchList);
        }
    }
}


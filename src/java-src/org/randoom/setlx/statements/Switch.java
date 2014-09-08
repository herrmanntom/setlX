package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statementBranches.SwitchAbstractBranch;
import org.randoom.setlx.statementBranches.SwitchDefaultBranch;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.List;

/**
 * The switch statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
 *     ;
 *
 * implemented with different classes which inherit from BranchAbstract:
 *                     ==========================    ===================
 *                          SwitchCaseBranch         SwitchDefaultBranch
 */
public class Switch extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Switch.class);

    private final List<SwitchAbstractBranch> branchList;

    /**
     * Create a new switch statement.
     *
     * @param branchList List of switch branches.
     */
    public Switch(final List<SwitchAbstractBranch> branchList) {
        this.branchList = branchList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        for (final SwitchAbstractBranch br : branchList) {
            if (br.evalConditionToBool(state)) {
                return br.getStatements().execute(state);
            }
        }
        return null;
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // binding inside an switch are only valid if present in all branches
        // and last branch is a default-branch
        final int      preBound  = boundVariables.size();
        List<String> boundHere = null;
        for (final SwitchAbstractBranch br : branchList) {
            final List<String> boundTmp = new ArrayList<String>(boundVariables);

            br.collectVariablesAndOptimize(state, boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<String>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (branchList.get(branchList.size() - 1) instanceof SwitchDefaultBranch) {
            boundVariables.addAll(boundHere);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("switch {");
        sb.append(state.getEndl());
        for (final SwitchAbstractBranch br : branchList) {
            br.appendString(state, sb, tabs + 1);
        }
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList branchList = new SetlList(this.branchList.size());
        for (final SwitchAbstractBranch br: this.branchList) {
            branchList.addMember(state, br.toTerm(state));
        }
        result.addMember(state, branchList);

        return result;
    }

    /**
     * Convert a term representing an if-then-else statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting if-then-else Statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Switch termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList                   branches   = (SetlList) term.firstMember();
            final List<SwitchAbstractBranch> branchList = new ArrayList<SwitchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(SwitchAbstractBranch.valueToSwitchAbstractBranch(state, v));
            }
            return new Switch(branchList);
        }
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the if-then-else statement, which uses several classes to
 * represent the different kind of branches.
 *
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
 *     ;
 *
 * implemented with different classes which inherit from BranchAbstract:
 *       ====================================  ===========================================    ====================
 *                   IfThenBranch                          IfThenElseIfBranch                   IfThenElseBranch
 */
public class IfThen extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(IfThen.class);

    private final List<IfThenAbstractBranch> branchList;

    public IfThen(final List<IfThenAbstractBranch> branchList) {
        this.branchList = branchList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        try {
            // increase callStackDepth
            ++(state.callStackDepth);

            for (final IfThenAbstractBranch br : branchList) {
                if (br.evalConditionToBool(state)) {
                    return br.execute(state);
                }
            }
            return null;
        } catch (final StackOverflowError soe) {
            state.storeFirstCallStackDepth();
            throw soe;
        } finally {
            // decrease callStackDepth
            --(state.callStackDepth);
        }
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // binding inside an if-then-else are only valid if present in all branches
        // and last branch is an else-branch
        final int    preBound  = boundVariables.size();
        List<String> boundHere = null;
        for (final IfThenAbstractBranch br : branchList) {
            final List<String> boundTmp = new ArrayList<String>(boundVariables);

            br.collectVariablesAndOptimize(boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<String>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (branchList.get(branchList.size() - 1) instanceof IfThenElseBranch) {
            boundVariables.addAll(boundHere);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        for (final IfThenAbstractBranch br : branchList) {
            br.appendString(state, sb, tabs);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term     result     = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList branchList = new SetlList(this.branchList.size());
        for (final IfThenAbstractBranch br: this.branchList) {
            branchList.addMember(state, br.toTerm(state));
        }
        result.addMember(state, branchList);

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


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statementBranches.AbstractIfThenBranch;
import org.randoom.setlx.statementBranches.IfThenElseBranch;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

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
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(IfThen.class);

    private final FragmentList<AbstractIfThenBranch> branchList;

    /**
     * Create a new if-then-else statement.
     *
     * @param branchList List of if-then-else branches.
     */
    public IfThen(final FragmentList<AbstractIfThenBranch> branchList) {
        this.branchList = branchList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        for (final AbstractIfThenBranch br : branchList) {
            if (br.evalConditionToBool(state)) {
                return br.getStatements().execute(state);
            }
        }
        return null;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // binding inside an if-then-else are only valid if present in all branches
        // and last branch is an else-branch
        final int    preBound  = boundVariables.size();
        List<String> boundHere = null;
        for (final AbstractIfThenBranch br : branchList) {
            final List<String> boundTmp = new ArrayList<String>(boundVariables);

            br.collectVariablesAndOptimize(state, boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<String>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (boundHere != null && branchList.get(branchList.size() - 1) instanceof IfThenElseBranch) {
            boundVariables.addAll(boundHere);
        }
        return false;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        for (final AbstractIfThenBranch br : branchList) {
            br.appendString(state, sb, tabs);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term     result     = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList branchList = new SetlList(this.branchList.size());
        for (final AbstractIfThenBranch br: this.branchList) {
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
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static IfThen termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList                           branches   = (SetlList) term.firstMember();
            final FragmentList<AbstractIfThenBranch> branchList = new FragmentList<AbstractIfThenBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(AbstractIfThenBranch.valueToIfThenAbstractBranch(state, v));
            }
            return new IfThen(branchList);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == IfThen.class) {
            return branchList.compareTo(((IfThen) other).branchList);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(IfThen.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == IfThen.class) {
            return branchList.equals(((IfThen) obj).branchList);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + branchList.hashCode();
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statementBranches.AbstractTryCatchBranch;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.List;

/**
 * The try-catch statement, that catches exceptions in SetlX.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
 *     ;
 *
 * implemented here as:
 *                 =====      ==============================================================================================================================
 *               blockToTry                                                              tryList
 *
 * implemented with different classes which inherit from BranchTryAbstract:
 *                            ======================================   =========================================   =========================================
 *                                        TryCatchBranch                           TryCatchLngBranch                           TryCatchUsrBranch
 */
public class TryCatch extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(TryCatch.class);

    private final Block                                blockToTry;
    private final FragmentList<AbstractTryCatchBranch> tryList;

    /**
     * Create a new try-catch statement.
     *
     * @param blockToTry Block of statement to "try"
     * @param tryList    List of catch branches.
     */
    public TryCatch(final Block blockToTry, final FragmentList<AbstractTryCatchBranch> tryList) {
        this.blockToTry = blockToTry;
        this.tryList    = tryList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        try {
            return blockToTry.execute(state);
        } catch (final CatchableInSetlXException cise) {
            for (final AbstractTryCatchBranch br : tryList) {
                if (br.catches(state, cise)) {
                    return br.execute(state, cise);
                }
            }
            // If we get here nothing matched. Re-throw as if nothing happened
            throw cise;
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        blockToTry.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        // catch blocks cannot be trusted to assign anything in any case
        final int preBound = boundVariables.size();
        for (final AbstractTryCatchBranch br : tryList) {
            br.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        while (boundVariables.size() > preBound) {
            boundVariables.remove(boundVariables.size() - 1);
        }
        return false;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("try ");
        blockToTry.appendString(state, sb, tabs, true);
        for (final AbstractTryCatchBranch br : tryList) {
            br.appendString(state, sb, tabs);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, blockToTry.toTerm(state));

        final SetlList branchList = new SetlList(tryList.size());
        for (final AbstractTryCatchBranch br: tryList) {
            branchList.addMember(state, br.toTerm(state));
        }
        result.addMember(state, branchList);

        return result;
    }

    /**
     * Convert a term representing a try-catch statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting if-then-else Statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static TryCatch termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Block                                block      = TermUtilities.valueToBlock(state, term.firstMember());
            final SetlList                             branches   = (SetlList) term.lastMember();
            final FragmentList<AbstractTryCatchBranch> branchList = new FragmentList<AbstractTryCatchBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(AbstractTryCatchBranch.valueToTryCatchAbstractBranch(state, v));
            }
            return new TryCatch(block, branchList);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == TryCatch.class) {
            TryCatch otr = (TryCatch) other;
            final int cmp = blockToTry.compareTo(otr.blockToTry);
            if (cmp != 0) {
                return cmp;
            }
            return tryList.compareTo(otr.tryList);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(TryCatch.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == TryCatch.class) {
            TryCatch otr = (TryCatch) obj;
            return blockToTry.equals(otr.blockToTry) && tryList.equals(otr.tryList);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + blockToTry.computeHashCode();
        hash = hash * 31 + tryList.hashCode();
        return hash;
    }
}


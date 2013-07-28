package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
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
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(TryCatch.class);

    private final Block                        blockToTry;
    private final List<TryCatchAbstractBranch> tryList;

    /**
     * Create a new try-catch statement.
     *
     * @param blockToTry Block of statement to "try"
     * @param tryList    List of catch branches.
     */
    public TryCatch(final Block blockToTry, final List<TryCatchAbstractBranch> tryList) {
        this.blockToTry = blockToTry;
        this.tryList    = tryList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        try{
            // increase callStackDepth
            state.callStackDepth += 2;

            return blockToTry.execute(state);
        } catch (final CatchableInSetlXException cise) {
            for (final TryCatchAbstractBranch br : tryList) {
                if (br.catches(state, cise)) {
                    return br.execute(state, cise);
                }
            }
            // If we get here nothing matched. Re-throw as if nothing happened
            throw cise;
        } finally {
            // decrease callStackDepth
            state.callStackDepth -= 2;
        }
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        blockToTry.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        // catch blocks cannot be trusted to assign anything in any case
        final int preBound = boundVariables.size();
        for (final TryCatchAbstractBranch br : tryList) {
            br.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
        while (boundVariables.size() > preBound) {
            boundVariables.remove(boundVariables.size() - 1);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("try ");
        blockToTry.appendString(state, sb, tabs, true);
        for (final TryCatchAbstractBranch br : tryList) {
            br.appendString(state, sb, tabs);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, blockToTry.toTerm(state));

        final SetlList branchList = new SetlList(tryList.size());
        for (final TryCatchAbstractBranch br: tryList) {
            branchList.addMember(state, br.toTerm(state));
        }
        result.addMember(state, branchList);

        return result;
    }

    /**
     * Convert a term representing a try-catch statement into such a statement.
     *
     * @param term                     Term to convert.
     * @return                         Resulting if-then-else Statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static TryCatch termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Block                           block       = TermConverter.valueToBlock(term.firstMember());
            final SetlList                        branches    = (SetlList) term.lastMember();
            final List<TryCatchAbstractBranch>    branchList  = new ArrayList<TryCatchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(TryCatchAbstractBranch.valueToTryCatchAbstractBranch(v));
            }
            return new TryCatch(block, branchList);
        }
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
    ;

implemented here as:
                =====      ==============================================================================================================================
             mBlockToTry                                                              mTryList

implemented with different classes which inherit from BranchTryAbstract:
                           ======================================   =========================================   =========================================
                                       TryCatchBranch                           TryCatchLngBranch                           TryCatchUsrBranch
*/

public class TryCatch extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^tryCatch";

    private final Block                        mBlockToTry;
    private final List<TryCatchAbstractBranch> mTryList;

    public TryCatch(final Block blockToTry, final List<TryCatchAbstractBranch> tryList) {
        mBlockToTry = blockToTry;
        mTryList    = tryList;
    }

    protected Value exec() throws SetlException {
        try{
            return mBlockToTry.execute();
        } catch (final CatchableInSetlXException cise) {
            for (final TryCatchAbstractBranch br : mTryList) {
                if (br.catches(cise)) {
                    return br.execute();
                }
            }
            // If we get here nothing matched. Re-throw as if nothing happened
            throw cise;
        }
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    protected void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mBlockToTry.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        for (final TryCatchAbstractBranch br : mTryList) {
            br.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("try ");
        mBlockToTry.appendString(sb, tabs, true);
        for (final TryCatchAbstractBranch br : mTryList) {
            br.appendString(sb, tabs);
        }
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(mBlockToTry.toTerm());

        final SetlList branchList = new SetlList(mTryList.size());
        for (final TryCatchAbstractBranch br: mTryList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

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


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
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

    private Block                        mBlockToTry;
    private List<TryCatchAbstractBranch> mTryList;

    public TryCatch(Block blockToTry, List<TryCatchAbstractBranch> tryList) {
        mBlockToTry = blockToTry;
        mTryList    = tryList;
    }

    public void exec() throws SetlException {
        try{
            mBlockToTry.execute();
        } catch (CatchableInSetlXException cise) {
            for (TryCatchAbstractBranch br : mTryList) {
                if (br.catches(cise)) {
                    br.execute();

                    return;

                }
            }
            // If we get here nothing matched. Re-throw as if nothing happened
            throw cise;
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(tabs);
        result += "try ";
        result += mBlockToTry.toString(tabs, true);
        for (TryCatchAbstractBranch br : mTryList) {
            result += br.toString(tabs);
        }
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(mBlockToTry.toTerm());

        SetlList branchList = new SetlList();
        for (TryCatchAbstractBranch br: mTryList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static TryCatch termToStatement(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Block                           block       = TermConverter.valueToBlock(term.firstMember());
            SetlList                        branches    = (SetlList) term.lastMember();
            List<TryCatchAbstractBranch>    branchList  = new ArrayList<TryCatchAbstractBranch>(branches.size());
            for (Value v : branches) {
                branchList.add(TryCatchAbstractBranch.valueToTryCatchAbstractBranch(v));
            }
            return new TryCatch(block, branchList);
        }
    }
}


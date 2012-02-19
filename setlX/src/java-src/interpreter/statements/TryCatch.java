package interpreter.statements;

import interpreter.exceptions.CatchableInSetlXException;
import interpreter.exceptions.SetlException;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.utilities.Environment;

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
                                       BranchTryCatch                           BranchTryCatchLng                           BranchTryCatchUsr
*/

public class TryCatch extends Statement {
    private Block                   mBlockToTry;
    private List<BranchTryAbstract> mTryList;

    public TryCatch(Block blockToTry, List<BranchTryAbstract> tryList) {
        mBlockToTry     = blockToTry;
        mTryList        = tryList;
    }

    public void execute() throws SetlException {
        try{
            mBlockToTry.execute();
        } catch (CatchableInSetlXException cise) {
            for (BranchTryAbstract br : mTryList) {
                if (br.catches(cise)) {
                    br.execute();

                    return;

                }
            }
            // If we get here nothing matched. Throw as if nothing happened
            throw cise;
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "try ";
        result += mBlockToTry.toString(tabs, true);
        for (BranchTryAbstract br : mTryList) {
            result += br.toString(tabs);
        }
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'tryCatch");
        result.addMember(mBlockToTry.toTerm());

        SetlList branchList = new SetlList();
        for (BranchTryAbstract br: mTryList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }
}


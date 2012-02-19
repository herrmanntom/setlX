package interpreter.statements;

import interpreter.exceptions.CatchableInSetlXException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.ThrownInSetlXException;
import interpreter.expressions.Variable;
import interpreter.types.SetlError;
import interpreter.types.Term;

/*

This catchUsr block catches any exception, which was user created, e.g.
by using  throw()  function in SetlX.

grammar rule:
statement
    : [...]
    | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
    ;

implemented here as:
                                                                                                                               ========         =====
                                                                                                                               mErrorVar   mBlockToRecover
*/

public class BranchTryCatchUsr extends BranchTryAbstract {
    private Variable                mErrorVar;
    private Block                   mBlockToRecover;
    private ThrownInSetlXException  mException;      // last catched exception

    public BranchTryCatchUsr(Variable errorVar, Block blockToRecover){
        mErrorVar       = errorVar;
        mBlockToRecover = blockToRecover;
    }

    public boolean catches(CatchableInSetlXException cise) {
        if (cise instanceof ThrownInSetlXException) {
            // store exception
            mException = (ThrownInSetlXException) cise;
            return true;
        } else {
            mException = null;
            return false;
        }
    }

    public void execute() throws SetlException {
        // assign directly
        mErrorVar.assign(mException.getValue());
        // remove stored exception
        mException = null;
        // execute
        mBlockToRecover.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = " catchUsr (" + mErrorVar.toString(tabs) + ") ";
        result += mBlockToRecover.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term    result  = new Term("'catchUsr");
        result.addMember(mErrorVar.toTerm());
        result.addMember(mBlockToRecover.toTerm());
        return result;
    }
}


package interpreter.statements;

import interpreter.exceptions.CatchableInSetlXException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.ThrownInSetlXException;
import interpreter.expressions.Variable;
import interpreter.types.SetlError;
import interpreter.types.Term;
import interpreter.utilities.TermConverter;

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

public class TryCatchUsrBranch extends TryCatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^tryCatchUsrBranch";

    private Variable                mErrorVar;
    private Block                   mBlockToRecover;
    private ThrownInSetlXException  mException;      // last catched exception

    public TryCatchUsrBranch(Variable errorVar, Block blockToRecover){
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

    public void exec() throws SetlException {
        execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = " catchUsr (" + mErrorVar.toString(tabs) + ") ";
        result += mBlockToRecover.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term    result  = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mErrorVar.toTerm());
        result.addMember(mBlockToRecover.toTerm());
        return result;
    }

    public static TryCatchUsrBranch termToBranch(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Variable    var     = Variable.termToExpr((Term) term.firstMember());
            Block       block   = TermConverter.valueToBlock(term.lastMember());
            return new TryCatchUsrBranch(var, block);
        }
    }
}


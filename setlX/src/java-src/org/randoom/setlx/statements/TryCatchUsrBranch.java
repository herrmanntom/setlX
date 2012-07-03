package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

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

    private final Variable               mErrorVar;
    private final Block                  mBlockToRecover;
    private       ThrownInSetlXException mException;      // last catched exception

    public TryCatchUsrBranch(final Variable errorVar, final Block blockToRecover){
        mErrorVar       = errorVar;
        mBlockToRecover = blockToRecover;
    }

    public boolean catches(final CatchableInSetlXException cise) {
        if (cise instanceof ThrownInSetlXException) {
            // store exception
            mException = (ThrownInSetlXException) cise;
            return true;
        } else {
            mException = null;
            return false;
        }
    }

    public Value execute() throws SetlException {
        // assign directly
        mErrorVar.assign(mException.getValue());
        // remove stored exception
        mException = null;
        // execute
        return mBlockToRecover.execute();
    }

    protected Value exec() throws SetlException {
        return execute();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(" catchUsr (");
        mErrorVar.appendString(sb, tabs);
        sb.append(") ");
        mBlockToRecover.appendString(sb, tabs, true);
    }

    /* term operations */

    public Term toTerm() {
        final Term    result  = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mErrorVar.toTerm());
        result.addMember(mBlockToRecover.toTerm());
        return result;
    }

    public static TryCatchUsrBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Variable  var     = Variable.termToExpr((Term) term.firstMember());
            final Block     block   = TermConverter.valueToBlock(term.lastMember());
            return new TryCatchUsrBranch(var, block);
        }
    }
}


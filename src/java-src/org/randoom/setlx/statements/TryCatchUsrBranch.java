package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

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

    public boolean catches(final State state, final CatchableInSetlXException cise) {
        if (cise instanceof ThrownInSetlXException) {
            // store exception
            mException = (ThrownInSetlXException) cise;
            return true;
        } else {
            mException = null;
            return false;
        }
    }

    public Value execute(final State state) throws SetlException {
        // assign directly
        mErrorVar.assign(state, mException.getValue().clone());
        // remove stored exception
        mException = null;
        // execute
        return mBlockToRecover.execute(state);
    }

    protected Value exec(final State state) throws SetlException {
        return execute(state);
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        // add all variables found to bound by not suppliying unboundVariables
        // as this expression is now used in an assignment
        mErrorVar.collectVariablesAndOptimize(boundVariables, boundVariables, boundVariables);

        mBlockToRecover.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(" catchUsr (");
        mErrorVar.appendString(sb, tabs);
        sb.append(") ");
        mBlockToRecover.appendString(sb, tabs, true);
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term    result  = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mErrorVar.toTerm(state));
        result.addMember(mBlockToRecover.toTerm(state));
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


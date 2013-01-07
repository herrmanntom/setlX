package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*

This catchLng block catches any exception, which was not user created, e.g.
represents a semantic or syntactic error.

grammar rule:
statement
    : [...]
    | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
    ;

implemented here as:
                                                                                   ========         =====
                                                                                   mErrorVar   mBlockToRecover
*/

public class TryCatchLngBranch extends TryCatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^tryCatchLngBranch";

    private final Variable                  mErrorVar;
    private final Block                     mBlockToRecover;
    private       CatchableInSetlXException mException;      // last catched exception

    public TryCatchLngBranch(final Variable errorVar, final Block blockToRecover){
        mErrorVar       = errorVar;
        mBlockToRecover = blockToRecover;
    }

    @Override
    public boolean catches(final State state, final CatchableInSetlXException cise) {
        if (cise instanceof ThrownInSetlXException) {
            mException = null;
            return false;
        } else {
            // store exception
            mException = cise;
            return true;
        }
    }

    @Override
    public ReturnMessage exec(final State state) throws SetlException {
        // wrap into error
        mErrorVar.assign(state, new SetlError(mException));
        // remove stored exception
        mException = null;
        // execute
        return mBlockToRecover.exec(state);
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        return exec(state);
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    @Override
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

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" catchLng (");
        mErrorVar.appendString(state, sb, tabs);
        sb.append(") ");
        mBlockToRecover.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term    result  = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, mErrorVar.toTerm(state));
        result.addMember(state, mBlockToRecover.toTerm(state));
        return result;
    }

    public static TryCatchLngBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Variable  var     = Variable.termToExpr((Term) term.firstMember());
            final Block     block   = TermConverter.valueToBlock(term.lastMember());
            return new TryCatchLngBranch(var, block);
        }
    }
}


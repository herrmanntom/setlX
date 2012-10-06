package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*

This catch block catches any exception, which is catchable in SetlX.

grammar rule:
statement
    : [...]
    | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
    ;

implemented here as:
                                       ========         =====
                                       mErrorVar   mBlockToRecover
*/

public class TryCatchBranch extends TryCatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^tryCatchBranch";

    private final Variable                  mErrorVar;
    private final Block                     mBlockToRecover;
    private       CatchableInSetlXException mException;      // last catched exception

    public TryCatchBranch(final Variable errorVar, final Block blockToRecover){
        mErrorVar       = errorVar;
        mBlockToRecover = blockToRecover;
    }

    public boolean catches(final CatchableInSetlXException cise) {
        // store exception
        mException = cise;
        return true;
    }

    public Value execute() throws SetlException {
        if (mException instanceof ThrownInSetlXException) {
            // assign directly
            mErrorVar.assign( ((ThrownInSetlXException) mException).getValue().clone() );
        } else {
            // wrap into error
            mErrorVar.assign(new SetlError(mException));
        }
        // remove stored exception
        mException = null;
        // execute
        return mBlockToRecover.execute();
    }

    protected Value exec() throws SetlException {
        return execute();
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
        sb.append(" catch (");
        mErrorVar.appendString(sb, tabs);
        sb.append(") ");
        mBlockToRecover.appendString(sb, tabs, true);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mErrorVar.toTerm());
        result.addMember(mBlockToRecover.toTerm());
        return result;
    }

    public static TryCatchBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Variable  var     = Variable.termToExpr((Term) term.firstMember());
            final Block     block   = TermConverter.valueToBlock(term.lastMember());
            return new TryCatchBranch(var, block);
        }
    }
}


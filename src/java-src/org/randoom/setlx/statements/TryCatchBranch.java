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

/**
 * This catch block catches any exception, which is catchable in SetlX.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
 *     ;
 *
 * implemented here as:
 *                                        ========         =====
 *                                        errorVar     blockToRecover
 *
 * TODO: implement in thread-save manor.
 */
public class TryCatchBranch extends TryCatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(TryCatchBranch.class);

    private final Variable                  errorVar;
    private final Block                     blockToRecover;
    private       CatchableInSetlXException exception;      // last caught exception

    public TryCatchBranch(final Variable errorVar, final Block blockToRecover){
        this.errorVar       = errorVar;
        this.blockToRecover = blockToRecover;
    }

    @Override
    public boolean catches(final State state, final CatchableInSetlXException cise) {
        // store exception
        exception = cise;
        return true;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        if (exception instanceof ThrownInSetlXException) {
            // assign directly
            errorVar.assign(state, ((ThrownInSetlXException) exception).getValue().clone(), FUNCTIONAL_CHARACTER);
        } else {
            // wrap into error
            errorVar.assign(state, new SetlError(exception), FUNCTIONAL_CHARACTER);
        }
        // remove stored exception
        exception = null;
        // execute
        return blockToRecover.execute(state);
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // add all variables found to bound by not supplying unboundVariables
        // as this expression is now used in an assignment
        errorVar.collectVariablesAndOptimize(boundVariables, boundVariables, boundVariables);

        blockToRecover.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" catch (");
        errorVar.appendString(state, sb, tabs);
        sb.append(") ");
        blockToRecover.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, errorVar.toTerm(state));
        result.addMember(state, blockToRecover.toTerm(state));
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


package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * This catchLng block catches any exception, which was not user created, e.g.
 * represents a semantic or syntactic error.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
 *     ;
 *
 * implemented here as:
 *                                                                                    ========         =====
 *                                                                                    errorVar     blockToRecover
 */
public class TryCatchLngBranch extends TryCatchAbstractBranch {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(TryCatchLngBranch.class);

    private final Variable errorVar;
    private final Block    blockToRecover;

    /**
     * Create new catchLng-branch.
     *
     * @param errorVar       Variable to bind caught exception to.
     * @param blockToRecover Statements to execute when exception is caught.
     */
    public TryCatchLngBranch(final Variable errorVar, final Block blockToRecover){
        this.errorVar       = errorVar;
        this.blockToRecover = blockToRecover;
    }

    @Override
    public boolean catches(final State state, final CatchableInSetlXException cise) {
        return !(cise instanceof ThrownInSetlXException);
    }

    @Override
    public ReturnMessage execute(final State state, final CatchableInSetlXException cise) throws SetlException {
        // wrap into error
        errorVar.assign(state, new SetlError(cise), FUNCTIONAL_CHARACTER);
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
        sb.append(" catchLng (");
        errorVar.appendString(state, sb, tabs);
        sb.append(") ");
        blockToRecover.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term    result  = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, errorVar.toTerm(state));
        result.addMember(state, blockToRecover.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing an catchLng-branch into such a branch.
     *
     * @param term                     Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static TryCatchLngBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Variable  var     = Variable.termToExpr((Term) term.firstMember());
            final Block     block   = TermConverter.valueToBlock(term.lastMember());
            return new TryCatchLngBranch(var, block);
        }
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    /*package*/ static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


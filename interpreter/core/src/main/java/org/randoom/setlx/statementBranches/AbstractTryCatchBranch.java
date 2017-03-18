package org.randoom.setlx.statementBranches;

import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Base class for catch branches of the try-catch statement.
 */
public abstract class AbstractTryCatchBranch extends ImmutableCodeFragment {

    /** Variable to bind caught exception to.           */
    protected final AssignableVariable errorVar;
    /** Statements to execute when exception is caught. */
    protected final Block blockToRecover;

    /**
     * Create new catch-branch.
     *
     * @param errorVar       Variable to bind caught exception to.
     * @param blockToRecover Statements to execute when exception is caught.
     */
    protected AbstractTryCatchBranch(final AssignableVariable errorVar, final Block blockToRecover){
        this.errorVar       = errorVar;
        this.blockToRecover = blockToRecover;
    }

    @Override
    public final boolean collectVariablesAndOptimize (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
    ) {
        // add all variables found to bound by not supplying unboundVariables
        // as this expression is now used in an assignment
        errorVar.collectVariablesAndOptimize(state, boundVariables, boundVariables, boundVariables);

        blockToRecover.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        return false;
    }

    /**
     * Check if this branch catches the given exception
     *
     * @param state Current state of the running setlX program.
     * @param cise  Exception caught by the try-catch statement.
     * @return      True if this branch catches 'cise'.
     */
    public abstract boolean                catches(final State state, final CatchableInSetlXException cise);

    /**
     * Method executed after determining the correct catch branch.
     *
     * @param state          Current state of the running setlX program.
     * @param cise           Exception caught by the try-catch statement.
     * @return               Result of the execution (e.g. return value, continue, etc).
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public final ReturnMessage execute(final State state, final CatchableInSetlXException cise) throws SetlException {
        if (cise instanceof ThrownInSetlXException) {
            // assign directly
            errorVar.assignUncloned(state, ((ThrownInSetlXException) cise).getValue().clone(), getFunctionalCharacter());
        } else {
            // wrap into error
            errorVar.assignUncloned(state, new SetlError(cise), getFunctionalCharacter());
        }
        // execute
        return blockToRecover.execute(state);
    }

    /* term operations */

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    protected abstract String getFunctionalCharacter();

    @Override
    public final Term toTerm(final State state) throws SetlException {
        final Term result = new Term(getFunctionalCharacter(), 2);
        result.addMember(state, errorVar.toTerm(state));
        result.addMember(state, blockToRecover.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a catch branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static AbstractTryCatchBranch valueToTryCatchAbstractBranch(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed AbstractTryCatchBranch");
        } else {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            if        (fc.equals(TryCatchBranch.functionalCharacter())) {
                return TryCatchBranch.termToBranch(state, term);
            } else if (fc.equals(TryCatchLngBranch.functionalCharacter())) {
                return TryCatchLngBranch.termToBranch(state, term);
            } else if (fc.equals(TryCatchUsrBranch.functionalCharacter())) {
                return TryCatchUsrBranch.termToBranch(state, term);
            } else {
                throw new TermConversionException("malformed AbstractTryCatchBranch");
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (this.getClass() == other.getClass()) {
            AbstractTryCatchBranch otr = (AbstractTryCatchBranch) other;
            final int cmp = errorVar.compareTo(otr.errorVar);
            if (cmp != 0) {
                return cmp;
            }
            return blockToRecover.compareTo(otr.blockToRecover);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            AbstractTryCatchBranch otr = (AbstractTryCatchBranch) obj;
            return errorVar.equals(otr.errorVar) && blockToRecover.equals(otr.blockToRecover);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) compareToOrdering()) + errorVar.computeHashCode();
        hash = hash * 31 + blockToRecover.computeHashCode();
        return hash;
    }
}


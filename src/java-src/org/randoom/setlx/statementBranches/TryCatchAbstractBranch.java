package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

/**
 * Base class for catch branches of the try-catch statement.
 */
public abstract class TryCatchAbstractBranch extends CodeFragment {

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
    public abstract ReturnMessage          execute(final State state, final CatchableInSetlXException cise) throws SetlException;

    /**
     * Convert a term representing a catch branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static   TryCatchAbstractBranch valueToTryCatchAbstractBranch(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed TryCatchAbstractBranch");
        } else {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            if        (fc.equals(TryCatchBranch.getFunctionalCharacter())) {
                return TryCatchBranch.termToBranch(state, term);
            } else if (fc.equals(TryCatchLngBranch.getFunctionalCharacter())) {
                return TryCatchLngBranch.termToBranch(state, term);
            } else if (fc.equals(TryCatchUsrBranch.getFunctionalCharacter())) {
                return TryCatchUsrBranch.termToBranch(state, term);
            } else {
                throw new TermConversionException("malformed TryCatchAbstractBranch");
            }
        }
    }
}


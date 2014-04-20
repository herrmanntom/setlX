package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Base class for branches of the if-then-else statement.
 */
public abstract class IfThenAbstractBranch extends AbstractBranch {

    /**
     * Convert a term representing an if-then-else branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static   IfThenAbstractBranch valueToIfThenAbstractBranch(final State state, final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed IfThenAbstractBranch");
        } else {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            if        (fc.equals(IfThenBranch.getFunctionalCharacter())) {
                return IfThenBranch.termToBranch(state, term);
            } else if (fc.equals(IfThenElseIfBranch.getFunctionalCharacter())) {
                return IfThenElseIfBranch.termToBranch(state, term);
            } else if (fc.equals(IfThenElseBranch.getFunctionalCharacter())) {
                return IfThenElseBranch.termToBranch(state, term);
            } else {
                throw new TermConversionException("malformed IfThenAbstractBranch");
            }
        }
    }
}


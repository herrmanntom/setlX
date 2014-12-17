package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Base class for branches of the switch statement.
 */
public abstract class AbstractSwitchBranch extends AbstractBranch {

    /**
     * Convert a term representing an if-then-else branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static AbstractSwitchBranch valueToSwitchAbstractBranch(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed AbstractSwitchBranch");
        } else {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            if        (fc.equals(SwitchCaseBranch.getFunctionalCharacter())) {
                return SwitchCaseBranch.termToBranch(state, term);
            } else if (fc.equals(SwitchDefaultBranch.getFunctionalCharacter())) {
                return SwitchDefaultBranch.termToBranch(state, term);
            } else {
                throw new TermConversionException("malformed AbstractSwitchBranch");
            }
        }
    }

}


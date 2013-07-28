package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

/**
 * Base class for branches of the switch statement.
 */
public abstract class SwitchAbstractBranch extends AbstractBranch {

    /**
     * Convert a term representing an if-then-else branch into such a branch.
     *
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static   SwitchAbstractBranch valueToSwitchAbstractBranch(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed SwitchAbstractBranch");
        } else {
            final Term    term    = (Term) value;
            final String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(SwitchCaseBranch.getFunctionalCharacter())) {
                return SwitchCaseBranch.termToBranch(term);
            } else if (fc.equals(SwitchDefaultBranch.getFunctionalCharacter())) {
                return SwitchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed SwitchAbstractBranch");
            }
        }
    }

}


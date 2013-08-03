package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

/**
 * Base class for branches of the if-then-else statement.
 */
public abstract class IfThenAbstractBranch extends AbstractBranch {

    /**
     * Convert a term representing an if-then-else branch into such a branch.
     *
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static   IfThenAbstractBranch valueToIfThenAbstractBranch(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed IfThenAbstractBranch");
        } else {
            final Term    term    = (Term) value;
            final String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(IfThenBranch.getFunctionalCharacter())) {
                return IfThenBranch.termToBranch(term);
            } else if (fc.equals(IfThenElseIfBranch.getFunctionalCharacter())) {
                return IfThenElseIfBranch.termToBranch(term);
            } else if (fc.equals(IfThenElseBranch.getFunctionalCharacter())) {
                return IfThenElseBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed IfThenAbstractBranch");
            }
        }
    }
}


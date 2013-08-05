package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * Base class for branches of the match statement.
 */
public abstract class MatchAbstractBranch extends AbstractBranch {

    /**
     * Evaluate the match-condition of this branch and return the result.
     * (To be implemented by classes representing actual branches)
     *
     * @param state          Current state of the running setlX program.
     * @param term           Term to match.
     * @return               Result of the match.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract MatchResult         matches(final State state, final Value term)   throws SetlException;

    /**
     * Convert a term representing a match branch into such a branch.
     *
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static   MatchAbstractBranch valueToMatchAbstractBranch(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed MatchAbstractBranch");
        } else {
            final Term    term    = (Term) value;
            final String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(MatchCaseBranch.getFunctionalCharacter())) {
                return MatchCaseBranch.termToBranch(term);
            } else if (fc.equals(MatchRegexBranch.getFunctionalCharacter())) {
                return MatchRegexBranch.termToBranch(term);
            } else if (fc.equals(MatchDefaultBranch.getFunctionalCharacter())) {
                return MatchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed MatchAbstractBranch");
            }
        }
    }
}


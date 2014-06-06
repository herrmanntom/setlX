package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ScanResult;
import org.randoom.setlx.utilities.State;

/**
 * Base class for branches of the scan statement.
 */
public abstract class MatchAbstractScanBranch extends MatchAbstractBranch {
    /**
     * Evaluate the scan-condition of this branch and return the result.
     * (To be implemented by classes representing actual branches)
     *
     * @param state          Current state of the running setlX program.
     * @param string         String to scan.
     * @return               Result of the scan.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract ScanResult scannes(final State state, SetlString string) throws SetlException;

    /**
     * Convert a term representing a match branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static   MatchAbstractScanBranch valueToMatchAbstractScanBranch(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed MatchAbstractScanBranch");
        } else {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            if (fc.equals(MatchRegexBranch.getFunctionalCharacter())) {
                return MatchRegexBranch.termToBranch(state, term);
            } else if (fc.equals(MatchDefaultBranch.getFunctionalCharacter())) {
                return MatchDefaultBranch.termToBranch(state, term);
            } else {
                throw new TermConversionException("malformed MatchScanAbstractScanBranch");
            }
        }
    }
}


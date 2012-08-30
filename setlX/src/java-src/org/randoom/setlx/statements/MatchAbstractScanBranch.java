package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;

public abstract class MatchAbstractScanBranch extends MatchAbstractBranch {

    public abstract MatchResult scannes(SetlString string) throws IncompatibleTypeException;
    public abstract int         getEndOffset();

    public static   MatchAbstractScanBranch valueToMatchAbstractScanBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed MatchAbstractScanBranch");
        } else {
            Term   term = (Term) value;
            String fc   = term.functionalCharacter().getUnquotedString();
            if (fc.equals(MatchRegexBranch.FUNCTIONAL_CHARACTER)) {
                return MatchRegexBranch.termToBranch(term);
            } else if (fc.equals(MatchDefaultBranch.FUNCTIONAL_CHARACTER)) {
                return MatchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed MatchScanAbstractScanBranch");
            }
        }
    }
}


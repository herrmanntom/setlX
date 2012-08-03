package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;

public abstract class MatchAbstractBranch extends Statement {

    public abstract MatchResult         matches(Value term)   throws IncompatibleTypeException;
    public abstract boolean             evalConditionToBool() throws SetlException;

    public static   MatchAbstractBranch valueToMatchAbstractBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed MatchAbstractBranch");
        } else {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(MatchCaseBranch.FUNCTIONAL_CHARACTER)) {
                return MatchCaseBranch.termToBranch(term);
            } else if (fc.equals(MatchSplitListBranch.FUNCTIONAL_CHARACTER)) {
                return MatchSplitListBranch.termToBranch(term);
            } else if (fc.equals(MatchSplitSetBranch.FUNCTIONAL_CHARACTER)) {
                return MatchSplitSetBranch.termToBranch(term);
            } else if (fc.equals(MatchDefaultBranch.FUNCTIONAL_CHARACTER)) {
                return MatchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed MatchAbstractBranch");
            }
        }
    }
}


package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

public abstract class MatchAbstractBranch extends Statement {

    public abstract MatchResult         matches(final State state, final Value term)   throws SetlException;
    public abstract boolean             evalConditionToBool(final State state) throws SetlException;

    public static   MatchAbstractBranch valueToMatchAbstractBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed MatchAbstractBranch");
        } else {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(MatchCaseBranch.FUNCTIONAL_CHARACTER)) {
                return MatchCaseBranch.termToBranch(term);
            } else if (fc.equals(MatchRegexBranch.FUNCTIONAL_CHARACTER)) {
                return MatchRegexBranch.termToBranch(term);
            } else if (fc.equals(MatchDefaultBranch.FUNCTIONAL_CHARACTER)) {
                return MatchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed MatchAbstractBranch");
            }
        }
    }
}


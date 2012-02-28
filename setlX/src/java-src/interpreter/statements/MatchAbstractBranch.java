package interpreter.statements;

import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.MatchResult;

public abstract class MatchAbstractBranch extends Statement {

    public abstract MatchResult         matches(Value term);

    public static   MatchAbstractBranch valueToMatchAbstractBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed MatchAbstractBranch");
        } else {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(MatchCaseBranch.FUNCTIONAL_CHARACTER)) {
                return MatchCaseBranch.termToBranch(term);
            } else if (fc.equals(MatchDefaultBranch.FUNCTIONAL_CHARACTER)) {
                return MatchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed MatchAbstractBranch");
            }
        }
    }

}


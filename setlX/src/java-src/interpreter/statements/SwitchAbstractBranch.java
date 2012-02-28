package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;

public abstract class SwitchAbstractBranch extends Statement {

    public abstract boolean 				evalConditionToBool() throws SetlException;

    public static   SwitchAbstractBranch	valueToSwitchAbstractBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed SwitchAbstractBranch");
        } else {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(SwitchCaseBranch.FUNCTIONAL_CHARACTER)) {
                return SwitchCaseBranch.termToBranch(term);
            } else if (fc.equals(SwitchDefaultBranch.FUNCTIONAL_CHARACTER)) {
                return SwitchDefaultBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed SwitchAbstractBranch");
            }
        }
    }

}


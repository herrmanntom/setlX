package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;

public abstract class IfThenAbstractBranch extends Statement {

    public abstract boolean                 evalConditionToBool() throws SetlException;

    public static   IfThenAbstractBranch    valueToIfThenAbstractBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed IfThenAbstractBranch");
        } else {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(IfThenBranch.FUNCTIONAL_CHARACTER)) {
                return IfThenBranch.termToBranch(term);
            } else if (fc.equals(IfThenElseIfBranch.FUNCTIONAL_CHARACTER)) {
                return IfThenElseIfBranch.termToBranch(term);
            } else if (fc.equals(IfThenElseBranch.FUNCTIONAL_CHARACTER)) {
                return IfThenElseBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed IfThenAbstractBranch");
            }
        }
    }

}


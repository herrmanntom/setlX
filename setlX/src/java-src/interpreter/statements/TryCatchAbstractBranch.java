package interpreter.statements;

import interpreter.exceptions.CatchableInSetlXException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;

public abstract class TryCatchAbstractBranch extends Statement {

    public abstract boolean 				catches(CatchableInSetlXException cise);

    public static   TryCatchAbstractBranch	valueToTryCatchAbstractBranch(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed TryCatchAbstractBranch");
        } else {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if        (fc.equals(TryCatchBranch.FUNCTIONAL_CHARACTER)) {
                return TryCatchBranch.termToBranch(term);
            } else if (fc.equals(TryCatchLngBranch.FUNCTIONAL_CHARACTER)) {
                return TryCatchLngBranch.termToBranch(term);
            } else if (fc.equals(TryCatchUsrBranch.FUNCTIONAL_CHARACTER)) {
                return TryCatchUsrBranch.termToBranch(term);
            } else {
                throw new TermConversionException("malformed TryCatchAbstractBranch");
            }
        }
    }

}


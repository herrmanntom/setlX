package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class TryCatchAbstractBranch extends Statement {

    public abstract boolean                 catches(final State state, final CatchableInSetlXException cise);

    public static   TryCatchAbstractBranch  valueToTryCatchAbstractBranch(final Value value) throws TermConversionException {
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


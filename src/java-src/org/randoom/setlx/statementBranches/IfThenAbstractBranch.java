package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class IfThenAbstractBranch extends Statement {

    public abstract boolean                 evalConditionToBool(final State state) throws SetlException;

    public static   IfThenAbstractBranch    valueToIfThenAbstractBranch(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed IfThenAbstractBranch");
        } else {
            final Term    term    = (Term) value;
            final String  fc      = term.functionalCharacter().getUnquotedString();
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


package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class SwitchAbstractBranch extends Statement {

    public abstract boolean                 evalConditionToBool(final State state) throws SetlException;

    public static   SwitchAbstractBranch    valueToSwitchAbstractBranch(final Value value) throws TermConversionException {
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


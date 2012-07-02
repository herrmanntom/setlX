package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    ;

implemented here as:
                                                                                                      =====
                                                                                                   mStatements
*/

public class IfThenElseBranch extends IfThenAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^ifThenElseBranch";

    private final Block mStatements;

    public IfThenElseBranch(final Block statements){
        mStatements = statements;
    }

    public boolean evalConditionToBool() {
        return true;
    }

    public Value execute() throws SetlException {
        return mStatements.execute();
    }

    protected Value exec() throws SetlException {
        return execute();
    }

    /* string operations */

    public String toString(final int tabs) {
        String result = " else ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static IfThenElseBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Block block = TermConverter.valueToBlock(term.firstMember());
            return new IfThenElseBranch(block);
        }
    }
}


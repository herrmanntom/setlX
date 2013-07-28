package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the else-branch.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
 *     ;
 *
 * implemented here as:
 *                                                                                                       =====
 *                                                                                                     statements
 */
public class IfThenElseBranch extends IfThenAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(IfThenElseBranch.class);

    private final Block statements;

    public IfThenElseBranch(final Block statements){
        this.statements = statements;
    }

    @Override
    public boolean evalConditionToBool(final State state) {
        return true;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        return statements.execute(state);
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        statements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" else ");
        statements.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, statements.toTerm(state));
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


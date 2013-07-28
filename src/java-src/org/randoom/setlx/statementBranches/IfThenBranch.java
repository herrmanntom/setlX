package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the if-(??)-then-branch.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
 *     ;
 *
 * implemented here as:
 *                =========         =====
 *                condition       statements
 */
public class IfThenBranch extends IfThenAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(IfThenBranch.class);

    private final Condition condition;
    private final Block     statements;

    public IfThenBranch(final Condition condition, final Block statements){
        this.condition  = condition;
        this.statements = statements;
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        return condition.eval(state) == SetlBoolean.TRUE;
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
        condition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        statements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("if (");
        condition.appendString(state, sb, tabs);
        sb.append(") ");
        statements.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, condition.toTerm(state));
        result.addMember(state, statements.toTerm(state));
        return result;
    }

    public static IfThenBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new IfThenBranch(condition, block);
        }
    }
}


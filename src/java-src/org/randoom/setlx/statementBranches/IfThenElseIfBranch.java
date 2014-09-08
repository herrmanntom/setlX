package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the else-if-(??)-then-branch.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
 *     ;
 *
 * implemented here as:
 *                                                             =========         =====
 *                                                             condition       statements
 */
public class IfThenElseIfBranch extends IfThenAbstractBranch {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(IfThenElseIfBranch.class);

    private final Condition condition;
    private final Block     statements;

    /**
     * Create new else-if-(??)-then-branch.
     *
     * @param condition  Condition to check before execution.
     * @param statements Statements to execute when condition is met.
     */
    public IfThenElseIfBranch(final Condition condition, final Block statements){
        this.condition  = condition;
        this.statements = statements;
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        return condition.eval(state) == SetlBoolean.TRUE;
    }

    @Override
    public Block getStatements() {
        return statements;
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        condition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        statements.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" else if (");
        condition.appendString(state, sb, tabs);
        sb.append(") ");
        statements.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, condition.toTerm(state));
        result.addMember(state, statements.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing an else-if-(??)-then-branch branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static IfThenElseIfBranch termToBranch(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition = TermConverter.valueToCondition(state, term.firstMember());
            final Block     block     = TermConverter.valueToBlock(state, term.lastMember());
            return new IfThenElseIfBranch(condition, block);
        }
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    /*package*/ static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


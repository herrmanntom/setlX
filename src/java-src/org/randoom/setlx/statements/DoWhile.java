package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * The do-while statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'do' '{' block '}' 'while' '(' condition ')' ';'
 *     ;
 *
 * implemented here as:
 *                =====                 =========
 *              statements              condition
 */
public class DoWhile extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(DoWhile.class);

    private final Condition condition;
    private final Block     statements;

    /**
     * Create a new do-while statement.
     *
     * @param condition  Loop-condition.
     * @param statements Statements to execute inside the loop.
     */
    public DoWhile(final Condition condition, final Block statements) {
        this.condition  = condition;
        this.statements = statements;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        ReturnMessage result = null;
        do {
            result = statements.execute(state);
            if (result != null) {
                if (result == ReturnMessage.CONTINUE) {
                    continue;
                } else if (result == ReturnMessage.BREAK) {
                    break;
                }
                return result;
            }
        } while (condition.eval(state) == SetlBoolean.TRUE);
        return null;
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
        sb.append("do ");
        condition.appendString(state, sb, tabs);
        statements.appendString(state, sb, tabs, true);
        sb.append("while (");
        condition.appendString(state, sb, tabs);
        sb.append(");");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, condition.toTerm(state));
        result.addMember(state, statements.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a DoWhile statement into such a statement.
     *
     * @param term                     Term to convert.
     * @return                         Resulting DoWhile Statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static DoWhile termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new DoWhile(condition, block);
        }
    }
}


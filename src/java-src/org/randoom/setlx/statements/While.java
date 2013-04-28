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
 * Everyones favorite: the while statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'while' '(' condition ')' '{' block '}'
 *     ;
 *
 * implemented here as:
 *                   =========         =====
 *                   condition       statements
 */
public class While extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(While.class);

    private final Condition condition;
    private final Block     statements;

    public While(final Condition condition, final Block statements) {
        this.condition  = condition;
        this.statements = statements;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        final boolean finishLoop  = state.isDebugFinishLoop;
        if (finishLoop) { // unset, because otherwise it would be reset when this loop finishes
            state.setDebugFinishLoop(false);
        }
        ReturnMessage result = null;
        while (condition.eval(state) == SetlBoolean.TRUE) {
            result = statements.exec(state);
            if (result != null) {
                if (result == ReturnMessage.CONTINUE) {
                    continue;
                } else if (result == ReturnMessage.BREAK) {
                    break;
                }
                return result;
            }
        }
        if (state.isDebugFinishLoop) {
            state.setDebugModeActive(true);
            state.setDebugFinishLoop(false);
        } else if (finishLoop) {
            state.setDebugFinishLoop(true);
        }
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
        sb.append("while (");
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

    public static While termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new While(condition, block);
        }
    }
}


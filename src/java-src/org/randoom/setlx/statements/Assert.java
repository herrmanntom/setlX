package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AssertException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the assert statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'assert' '(' condition ',' expr ')' ';'
 *     ;
 *
 * implemented here as:
 *                    =========     ====
 *                    condition    message
 */
public class Assert extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Assert.class);

    private final Condition condition;
    private final Expr      message;

    public Assert(final Condition condition, final Expr message) {
        this.condition = condition;
        this.message   = message;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        if (condition.eval(state) != SetlBoolean.TRUE) {
            throw new AssertException("Assertion failed: " + message.eval(state).toString());
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
        message.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("assert(");
        condition.appendString(state, sb, tabs);
        sb.append(", ");
        message.appendString(state, sb, tabs);
        sb.append(");");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, condition.toTerm(state));
        result.addMember(state, message.toTerm(state));
        return result;
    }

    public static Assert termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Expr      message     = TermConverter.valueToExpr(term.lastMember());
            return new Assert(condition, message);
        }
    }
}


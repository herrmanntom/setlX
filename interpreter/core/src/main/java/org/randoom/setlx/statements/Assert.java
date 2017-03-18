package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AssertException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.Condition;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

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
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Assert.class);

    private final Condition condition;
    private final OperatorExpression message;

    /**
     * Create a new Assert statement.
     *
     * @param condition Condition to check before execution.
     * @param message   Message to throw as exception, when condition evaluates to false.
     */
    public Assert(final Condition condition, final OperatorExpression message) {
        this.condition = condition;
        this.message   = message;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        if (condition.evaluate(state) != SetlBoolean.TRUE) {
            throw new AssertException("Assertion failed: " + message.evaluate(state).toString(state));
        }
        return null;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return condition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
         && message.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
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
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, condition.toTerm(state));
        result.addMember(state, message.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing an Assert statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting statement of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Assert termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition = TermUtilities.valueToCondition(state, term.firstMember());
            final OperatorExpression message = OperatorExpression.createFromTerm(state, term.lastMember());
            return new Assert(condition, message);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Assert.class) {
            Assert otr = (Assert) other;
            final int cmp = condition.compareTo(otr.condition);
            if (cmp != 0) {
                return cmp;
            }
            return message.compareTo(otr.message);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Assert.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Assert.class) {
            Assert otr = (Assert) obj;
            return condition.equals(otr.condition) && message.equals(otr.message);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + condition.hashCode();
        hash = hash * 31 + message.hashCode();
        return hash;
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.Condition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

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
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(DoWhile.class);

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
        ReturnMessage result;
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
        } while (condition.evaluate(state) == SetlBoolean.TRUE);
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
            && statements.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
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
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, condition.toTerm(state));
        result.addMember(state, statements.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a DoWhile statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting DoWhile Statement.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static DoWhile termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition = TermUtilities.valueToCondition(state, term.firstMember());
            final Block     block     = TermUtilities.valueToBlock(state, term.lastMember());
            return new DoWhile(condition, block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == DoWhile.class) {
            DoWhile otr = (DoWhile) other;
            final int cmp = condition.compareTo(otr.condition);
            if (cmp != 0) {
                return cmp;
            }
            return statements.compareTo(otr.statements);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(DoWhile.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == DoWhile.class) {
            DoWhile otr = (DoWhile) obj;
            return condition.equals(otr.condition) && statements.equals(otr.statements);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + condition.hashCode();
        hash = hash * 31 + statements.computeHashCode();
        return hash;
    }
}


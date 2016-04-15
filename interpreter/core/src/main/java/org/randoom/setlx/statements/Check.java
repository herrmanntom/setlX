package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BacktrackException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.List;

/**
 * The check statement, which is used when implementing a backtrack-like
 * algorithm in conjunction with the backtrack-statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'check' '{' block '}' ('afterBacktrack' '{' block '}')?
 *     ;
 *
 * implemented here as:
 *                   =====                           =====
 *                 statements                       recovery
 */
public class Check extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Check.class);

    private final Block statements;
    private final Block recovery;

    /**
     * Create new Check statement.
     *
     * @param statements Statements to execute.
     * @param recovery   Statements to execute after backtracking.
     */
    public Check(final Block statements, final Block recovery) {
        this.statements = statements;
        this.recovery   = recovery;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        try {
            return statements.execute(state);
        } catch (final BacktrackException bte) {
            if (recovery != null) {
                return recovery.execute(state);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        statements.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        // bindings inside the recovery block are not always valid --- ignore them
        final int preBound = boundVariables.size();
        if (recovery != null) {
            recovery.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        while (boundVariables.size() > preBound) {
            boundVariables.remove(boundVariables.size() - 1);
        }
        return false;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("check ");
        statements.appendString(state,sb, tabs, true);
        if (recovery != null) {
            sb.append(" afterBacktrack ");
            recovery.appendString(state, sb, tabs, true);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, statements.toTerm(state));
        if (recovery != null) {
            result.addMember(state, recovery.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }
        return result;
    }

    /**
     * Re-generate a Check statement from a term.
     *
     * @param  state                   Current state of the running setlX program.
     * @param  term                    Term to regenerate from.
     * @return                         Check statement.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Check termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Block block    = TermUtilities.valueToBlock(state, term.firstMember());
                  Block recovery = null;
            if ( ! term.lastMember().equals(SetlString.NIL)) {
                recovery = TermUtilities.valueToBlock(state, term.lastMember());
            }
            return new Check(block, recovery);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Check.class) {
            Check otr = (Check) other;
            final int cmp = statements.compareTo(otr.statements);
            if (cmp != 0) {
                return cmp;
            }
            if (recovery != null) {
                if (otr.recovery != null) {
                    return recovery.compareTo(otr.recovery);
                } else {
                    return 1;
                }
            } else if (otr.recovery != null) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Check.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Check.class) {
            Check otr = (Check) obj;
            if (statements.equals(otr.statements)) {
                if (recovery != null && otr.recovery != null) {
                    return recovery.equals(otr.recovery);
                } else if (recovery == null && otr.recovery == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + statements.computeHashCode();
        if (recovery != null) {
            hash = hash * 31 + recovery.computeHashCode();
        }
        return hash;
    }
}


package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.List;

/**
 * The default branch in a match statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'match' '(' expr ')' '{' ( ... )* ('default' ':' block)? '}'
 *     ;
 *
 * implemented here as:
 *                                                        =====
 *                                                      statements
 */
public class MatchDefaultBranch extends AbstractMatchScanBranch {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(MatchDefaultBranch.class);
    /**
     * Offset returned when the default branch matched.
     */
    public  final static int    END_OFFSET           = -2020202020;

    private final Block statements;

    /**
     * Create new default-branch.
     *
     * @param statements Statements to execute.
     */
    public MatchDefaultBranch(final Block statements) {
        this.statements = statements;
    }

    @Override
    public MatchResult matches(final State state, final Value term) {
        return new MatchResult(true);
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        return true;
    }

    @Override
    public ScanResult scans(final State state, final SetlString string) {
        return new ScanResult(true, END_OFFSET);
    }

    @Override
    public Block getStatements() {
        return statements;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return statements.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("default:");
        sb.append(state.getEndl());
        statements.appendString(state, sb, tabs + 1);
        sb.append(state.getEndl());
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, statements.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a default-branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static MatchDefaultBranch termToBranch(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Block block = TermUtilities.valueToBlock(state, term.firstMember());
            return new MatchDefaultBranch(block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == MatchDefaultBranch.class) {
            return statements.compareTo(((MatchDefaultBranch) other).statements);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(MatchDefaultBranch.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == MatchDefaultBranch.class) {
            return statements.equals(((MatchDefaultBranch) obj).statements);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + statements.computeHashCode();
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


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.Iterator;
import java.util.List;

/**
 * A simple block of statements.
 *
 * grammar rules:
 * initBlock
 *     : statement+
 *     ;
 *
 * block
 *     : statement*
 *     ;
 *
 * implemented here as:
 *       =========
 *       statements
 */
public class Block extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Block.class);

    private final FragmentList<Statement> statements;

    /**
     * Create a new empty block of setlX statements.
     */
    public Block() {
        this(new FragmentList<Statement>());
    }

    /**
     * Create a new block containing exactly one setlX statement.
     *
     * @param statement Only statement of the block.
     */
    public Block(final Statement statement) {
        this(new FragmentList<Statement>(statement));
    }

    /**
     * Create a new block of setlX statements.
     *
     * @param statements Statements in the new block.
     */
    public Block(final FragmentList<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        ReturnMessage result;
        for (final Statement statement : statements) {
            if (state.executionStopped) {
                throw new StopExecutionException();
            }
            result = statement.execute(state);
            if (result != null) {
                return result;
            }
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
        boolean allowOptimization = true;
        for (final Statement statement : statements) {
            allowOptimization = statement.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;
        }
        return allowOptimization;
    }

    /**
     * Mark the last statement in this block to print its result after evaluation.
     *
     * Has no effect, when the last statement does not inherit from the
     * StatementWithPrintableResult class.
     *
     * @see org.randoom.setlx.statements.StatementWithPrintableResult
     */
    public void markLastExprStatement() {
        if (statements.size() > 0) {
            final Statement statement = statements.get(statements.size() - 1);
            if (statement instanceof StatementWithPrintableResult) {
                ((StatementWithPrintableResult) statement).setPrintAfterExecution();
            }
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        appendString(state, sb, tabs, false);
    }

    /**
     * Appends a string representation of this code fragment to the given
     * StringBuilder object.
     * Optionally adds brackets and indents statements in this block by
     * an additional level of tabs.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#appendString(State, StringBuilder, int)
     *
     * @param state    Current state of the running setlX program.
     * @param sb       StringBuilder to append to.
     * @param tabs     Number of tabs to use as indentation for statements.
     * @param brackets If true, brackets are added.
     */
    public void appendString(final State state, final StringBuilder sb, final int tabs, final boolean brackets) {
        final String endl      = state.getEndl();
              int    stmntTabs = tabs;
        if (brackets) {
            stmntTabs += 1;
            sb.append("{");
            sb.append(endl);
        }
        final Iterator<Statement> iterator = statements.iterator();
        while (iterator.hasNext()) {
            iterator.next().appendString(state, sb, stmntTabs);
            if (iterator.hasNext()) {
                sb.append(endl);
            }
        }
        if (brackets) {
            sb.append(endl);
            state.appendLineStart(sb, tabs);
            sb.append("}");
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList stmntList = new SetlList(statements.size());
        for (final Statement s: statements) {
            stmntList.addMember(state, s.toTerm(state));
        }
        result.addMember(state, stmntList);

        return result;
    }

    /**
     * Get the number of contained statements.
     *
     * @return number of contained statements.
     */
    public int size() {
        return statements.size();
    }

    /**
     * Convert a term representing a Block statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting statement of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Block termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList                stmnts = (SetlList) term.lastMember();
            final FragmentList<Statement> block  = new FragmentList<Statement>(stmnts.size());
            for (final Value v : stmnts) {
                block.add(createFromTerm(state, v));
            }
            return new Block(block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Block.class) {
            return statements.compareTo(((Block) other).statements);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SumAssignment.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Block.class) {
            return this.equalTo((Block) obj);
        }
        return false;
    }

    /**
     * Test if two Blocks are equal.
     * This operation is much faster as ( compareTo(other) == 0 ).
     *
     * @param other Other Blocks to compare to `this'
     * @return      True if `this' equals `other', false otherwise.
     */
    public boolean equalTo(final Block other) {
        return this == other || statements.equals(other.statements);
    }

    @Override
    public final int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + statements.hashCode();
    }
}


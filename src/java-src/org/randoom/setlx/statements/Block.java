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
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Block.class);
    // how deep can the call stack be, before checking to replace the stack
    private       static int    MAX_CALL_STACK_DEPTH = -1;

    private final FragmentList<Statement> statements;

    /**
     * Create a new empty block of setlX statements.
     */
    public Block() {
        this(new FragmentList<Statement>());
    }

    /**
     * Create a new empty block of setlX statements.
     *
     * @param size  Initial statement capacity of the block.
     */
    public Block(final int size) {
        this(new FragmentList<Statement>(size));
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
    public Block clone() {
        final Block clone = new Block(statements.size());
        clone.statements.addAll(statements);
        return clone;
    }

    /**
     * Add another statement to this block.
     *
     * @param stmnt statement to add
     */
    public void add(final Statement stmnt) {
        statements.add(stmnt);
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        // store and increase callStackDepth
        final int oldCallStackDepth = state.callStackDepth;
        state.callStackDepth += 2; // one for the block, one for the next statement

        boolean executeInCurrentStack = true;
        if (MAX_CALL_STACK_DEPTH < 0) {
            MAX_CALL_STACK_DEPTH = state.getMaxStackSize();
        }
        if (MAX_CALL_STACK_DEPTH > 0 && state.callStackDepth >= MAX_CALL_STACK_DEPTH) {
            executeInCurrentStack = false;
        }

        try {
            if (executeInCurrentStack) {
                ReturnMessage result;
                for (final Statement stmnt : statements) {
                    if (state.executionStopped) {
                        throw new StopExecutionException();
                    }
                    result = stmnt.execute(state);
                    if (result != null) {
                        return result;
                    }
                }
            } else {
                // prevent running out of stack by creating a new thread
                return new StatementRunner(this, state).execute();
            }
        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);
            throw soe;
        } finally {
            // reset callStackDepth
            state.callStackDepth = oldCallStackDepth;
        }
        return null;
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Statement stmnt : statements) {
            stmnt.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
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
            final Statement stmnt = statements.get(statements.size() - 1);
            if (stmnt instanceof StatementWithPrintableResult) {
                ((StatementWithPrintableResult) stmnt).setPrintAfterExecution();
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
        final Iterator<Statement> iter = statements.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, stmntTabs);
            if (iter.hasNext()) {
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
            final SetlList stmnts = (SetlList) term.lastMember();
            final Block    block  = new Block(stmnts.size());
            for (final Value v : stmnts) {
                block.add(TermConverter.valueToStatement(state, v));
            }
            return block;
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Block.class) {
            final FragmentList<Statement> otherStatements = ((Block) other).statements;
            if (statements == otherStatements) {
                return 0; // clone
            }
            final Iterator<Statement> iterFirst  = statements.iterator();
            final Iterator<Statement> iterSecond = otherStatements.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                final int cmp = iterFirst.next().compareTo(iterSecond.next());
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (iterFirst.hasNext()) {
                return 1;
            }
            if (iterSecond.hasNext()) {
                return -1;
            }
            return 0;
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


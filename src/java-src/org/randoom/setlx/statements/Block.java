package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
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

    private final List<Statement> statements;

    /**
     * Create a new empty block of setlX statements.
     */
    public Block() {
        this(new ArrayList<Statement>());
    }

    /**
     * Create a new empty block of setlX statements.
     *
     * @param size Initial statement capacity of the block.
     */
    public Block(final int size) {
        this(new ArrayList<Statement>(size));
    }

    /**
     * Create a new block of setlX statements.
     *
     * @param statements Statements in the new block.
     */
    public Block(final List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public Block clone() {
        final Block clone = new Block();
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
                ReturnMessage result = null;
                for (final Statement stmnt : statements) {
                    if (state.isExecutionStopped) {
                        throw new StopExecutionException("Interrupted");
                    }
                    result = stmnt.execute(state);
                    if (result != null) {
                        return result;
                    }
                }
            } else {
                // prevent running out of stack by creating a new thread
                final BlockExecThread callExec = new BlockExecThread(statements, state);

                try {
                    callExec.start();
                    callExec.join();
                    if (callExec.result != null) {
                        return callExec.result;
                    }
                } catch (final InterruptedException e) {
                    throw new StopExecutionException("Interrupted");
                }

                // handle exceptions thrown in thread
                if (callExec.error != null) {
                    if (callExec.error instanceof SetlException) {
                        throw (SetlException) callExec.error;
                    } else if (callExec.error instanceof StackOverflowError) {
                        throw (StackOverflowError) callExec.error;
                    } else if (callExec.error instanceof OutOfMemoryError) {
                        try {
                            // free some memory
                            state.resetState();
                            // give hint to the garbage collector
                            Runtime.getRuntime().gc();
                            // sleep a while
                            Thread.sleep(50);
                        } catch (final InterruptedException e) {
                            throw new StopExecutionException("Interrupted");
                        }
                        throw (OutOfMemoryError) callExec.error;
                    } else if (callExec.error instanceof RuntimeException) {
                        throw (RuntimeException) callExec.error;
                    }
                }
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
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Statement stmnt : statements) {
            stmnt.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
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
                ((StatementWithPrintableResult) stmnt).setPrintAfterEval();
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
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList stmntList = new SetlList(statements.size());
        for (final Statement s: statements) {
            stmntList.addMember(state, s.toTerm(state));
        }
        result.addMember(state, stmntList);

        return result;
    }

    /**
     * Convert a term to a Block-object.
     *
     * @param term                     Term to convert.
     * @return                         Resulting statement of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Block termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList    stmnts  = (SetlList) term.lastMember();
            final Block       block   = new Block(stmnts.size());
            for (final Value v : stmnts) {
                block.add(TermConverter.valueToStatement(v));
            }
            return block;
        }
    }

    // private subclass to cheat the end of the world... or stack, whatever comes first
    private class BlockExecThread extends Thread {
        private final List<Statement>                   statements;
        private final org.randoom.setlx.utilities.State state;
        /*package*/   ReturnMessage                     result;
        /*package*/   Throwable                         error;

        /*package*/ BlockExecThread(final List<Statement> statements, final org.randoom.setlx.utilities.State state) {
            this.statements = statements;
            this.state      = state;
            this.result     = null;
            this.error      = null;
        }

        @Override
        public void run() {
            try {
                state.callStackDepth  = 0;

                for (final Statement stmnt : statements) {
                    if (state.isExecutionStopped) {
                        throw new StopExecutionException("Interrupted");
                    }
                    result = stmnt.execute(state);
                    if (result != null) {
                        break;
                    }
                }
                error  = null;
            } catch (final SetlException se) {
                result = null;
                error  = se;
            } catch (final StackOverflowError soe) {
                result = null;
                error  = soe;
            } catch (final OutOfMemoryError oome) {
                result = null;
                error  = oome;
            } catch (final RuntimeException e) {
                result = null;
                error  = e;
            }
        }
    }
}


package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * Base class for branches.
 */
public abstract class AbstractBranch extends CodeFragment {

    /**
     * Evaluate the condition of this branch and return the result as Java boolean.
     * (To be implemented by classes representing actual branches)
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract boolean              evalConditionToBool(final State state) throws SetlException;

    /**
     * Get the statement block to execute when the condition evaluates to true.
     *
     * @return Block of setlX statements.
     */
    public abstract Block                getStatements();
}


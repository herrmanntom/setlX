package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Base class for all SetlX statements.
 */
public abstract class Statement extends CodeFragment {

    /**
     * Execute this statement.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the execution (e.g. return value, continue, etc).
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public          ReturnMessage exec(final State state) throws SetlException {
        if (state.isDebugModeActive && ! state.isDebugPromptActive()) {
            DebugPrompt.prompt(state, this);
            final ReturnMessage result = execute(state);
            return result;
        } else {
            return execute(state);
        }
    }

    /**
     * Execute-method to be implemented by classes representing actual statements.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the execution (e.g. return value, continue, etc).
     * @throws SetlException Thrown in case of some (user-) error.
     */
    protected abstract ReturnMessage execute(final State state) throws SetlException;

    @Override
    public abstract void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    /* string operations */

    @Override
    public abstract void appendString(final State state, final StringBuilder sb, final int tabs);

    /* term operations */

    @Override
    public abstract Value toTerm(final State state);
}


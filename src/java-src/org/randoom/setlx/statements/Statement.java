package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

public abstract class Statement extends CodeFragment {

    public          ReturnMessage exec(final State state) throws SetlException {
        if (state.isDebugModeActive && ! state.isDebugPromptActive()) {
            DebugPrompt.prompt(state, this);
            final ReturnMessage result = execute(state);
            return result;
        } else {
            return execute(state);
        }
    }

    protected abstract ReturnMessage execute(final State state) throws SetlException;

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
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


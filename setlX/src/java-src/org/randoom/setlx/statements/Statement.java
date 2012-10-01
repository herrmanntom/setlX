package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

public abstract class Statement extends CodeFragment {
    // is debug mode active? MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public static   boolean sDebugModeActive = false;

    public          Value execute() throws SetlException {
        if (sDebugModeActive && ! Environment.isDebugPromptActive()) {
            DebugPrompt.prompt(this);
            final Value result = exec();
            Expr.sStepNext = false;
            return result;
        } else {
            return exec();
        }
    }

    protected abstract Value exec() throws SetlException;

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    protected /*abstract*/ void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    )//;
    {
        // TODO: make abstract
        unboundVariables.add(new Variable("@123456"));
    }

    /* string operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    /* term operations */

    public abstract Value toTerm();
}


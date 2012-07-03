package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;

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

    /* string operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    /* term operations */

    public abstract Value toTerm();
}


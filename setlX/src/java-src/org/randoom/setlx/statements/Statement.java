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

    public          void execute() throws SetlException {
        if (sDebugModeActive && ! Environment.isDebugPromptActive()) {
            DebugPrompt.prompt(this);
            exec();
            Expr.sStepNext = false;
        } else {
            exec();
        }
    }

    public abstract void exec() throws SetlException;

    /* string operations */

    public abstract String toString(int tabs);

    public String toString() {
        return toString(0);
    }

    /* term operations */

    public abstract Value toTerm();
}


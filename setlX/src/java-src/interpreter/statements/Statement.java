package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.CodeFragment;
import interpreter.utilities.DebugPrompt;
import interpreter.utilities.Environment;

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


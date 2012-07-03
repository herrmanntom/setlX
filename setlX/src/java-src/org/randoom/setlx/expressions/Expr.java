package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;

public abstract class Expr extends CodeFragment {
    // step execution of this expr. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public static boolean sStepNext = false;

    public Value eval() throws SetlException {
        try {
            if (sStepNext && Environment.isDebugModeActive() && ! Environment.isDebugPromptActive()) {
                Environment.setDebugStepNextExpr(false);
                DebugPrompt.prompt(this);
            }
            return this.evaluate();
        } catch (AbortException ae) {
            throw ae;
        } catch (SetlException se) {
            se.addToTrace("Error in \"" + this + "\":");
            throw se;
        }
    }

    protected abstract Value evaluate() throws SetlException;

    /* sets this expression to the given value
       (only makes sense for variables and id-lists) */
    public Value assign(final Value v) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "This expression can not be used as target for assignments."
        );
    }

    /* string operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    /* term operations */

    public abstract Value toTerm();

    // toTerm when quoted ('@') expression is evaluated
    public          Value toTermQuoted() throws SetlException  {
        return toTerm();
    }

    // precedence level in SetlX-grammar
    public abstract int   precedence();
}


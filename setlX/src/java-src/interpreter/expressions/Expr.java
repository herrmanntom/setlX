package interpreter.expressions;

import interpreter.exceptions.AbortException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Value;
import interpreter.utilities.CodeFragment;
import interpreter.utilities.Environment;

public abstract class Expr extends CodeFragment {

    public abstract int getLineNr();

    public abstract void computeLineNr();

    public Value eval() throws SetlException {
        try {
            return this.evaluate();
        } catch (AbortException ae) {
            throw ae;
        } catch (SetlException se) {
            se.addToTrace("Error in \"" + this + "\":");
            throw se;
        }
    }

    public abstract Value evaluate() throws SetlException;

    /* sets this expression to the given value
       (only makes sense for variables and id-lists) */
    public Value assign(Value v) throws SetlException {
        throw new UndefinedOperationException("Error in \"" + this + "\":\n"
                                            + "This expression can not be used as target for assignments.");
    }

    /* string operations */

    public abstract String toString(int tabs);

    public final String toString() {
        return toString(0);
    }

    /* term operations */

    public abstract Value toTerm();

    // toTerm when quoted ('@') expression is evaluated
    public          Value toTermEvalArguments() throws SetlException {
        return toTerm();
    }

    // toTerm when quoted with '@'
    public          Value toTermQuoted() {
        return toTerm();
    }

    // precedence level in SetlX-grammar
    public abstract int   precedence();
}


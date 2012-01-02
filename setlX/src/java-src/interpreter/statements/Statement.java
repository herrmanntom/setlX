package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.CodeFragment;

public abstract class Statement extends CodeFragment {
    public abstract void execute() throws SetlException;

    /* string operations */

    public abstract String toString(int tabs);

    public String toString() {
        return toString(0);
    }

    /* term operations */

    public abstract Value toTerm();
}


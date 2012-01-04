package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

public abstract class CodeFragment {

    /* string operations */

    public abstract String toString(int tabs);

    /* term operations */

    public abstract Value toTerm() throws SetlException;
}


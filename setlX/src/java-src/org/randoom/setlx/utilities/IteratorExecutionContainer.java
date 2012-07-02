package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

public interface IteratorExecutionContainer {

    /* lastIterationValue is the very last value added to the environment
       before execution.                                                    */
    public Value execute(final Value lastIterationValue) throws SetlException;

}


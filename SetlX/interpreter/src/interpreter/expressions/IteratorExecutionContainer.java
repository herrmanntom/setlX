package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

public interface IteratorExecutionContainer {

    /* lastIterationValue is the very last value added to the environment
       before execution.                                                    */
    public void execute(Value lastIterationValue) throws SetlException ;

}



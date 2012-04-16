package interpreter.functions;

import interpreter.exceptions.ResetException;
import interpreter.types.Value;

import java.util.List;

// reset()                       : DEBUG: stop execution and return to interactive prompt

public class PD_reset extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_reset();

    private PD_reset() {
        super("reset");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws ResetException {
        throw new ResetException("reset");
    }
}


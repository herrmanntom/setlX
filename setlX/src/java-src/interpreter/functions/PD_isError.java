package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

// isError(value)          : test if value-type is error

public class PD_isError extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isError();

    private PD_isError() {
        super("isError");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isError();
    }
}


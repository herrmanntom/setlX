package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

// isReal(value)           : test if value-type is real

public class PD_isReal extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isReal();

    private PD_isReal() {
        super("isReal");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isReal();
    }
}


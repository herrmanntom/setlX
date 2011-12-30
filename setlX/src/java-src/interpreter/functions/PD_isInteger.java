package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

// isInteger(value)        : test if value-type is integer

public class PD_isInteger extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isInteger();

    private PD_isInteger() {
        super("isInteger");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isInteger();
    }
}


package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

// int(stringOrNumber)     : convert string or number into an integer, returns om on failure

public class PD_int extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_int();

    private PD_int() {
        super("int");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).toInteger();
    }
}


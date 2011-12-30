package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

import java.util.List;

// domain(map)             : get domain of map

public class PD_domain extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_domain();

    private PD_domain() {
        super("domain");
        addParameter("map");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).domain();
    }
}


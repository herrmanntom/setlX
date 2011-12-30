package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

import java.util.List;

// pow(set)                : computes the power-set

public class PD_pow extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_pow();

    private PD_pow() {
        super("pow");
        addParameter("set");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).powerSet();
    }
}


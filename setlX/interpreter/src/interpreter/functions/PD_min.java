package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_min extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_min();

    private PD_min() {
        super("min");
        addParameter(new ParameterDef("compoundValue"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).minimumMember();
    }
}


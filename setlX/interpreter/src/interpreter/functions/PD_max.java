package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_max extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_max();

    private PD_max() {
        super("max");
        addParameter(new ParameterDef("compoundValue"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).maximumMember();
    }
}


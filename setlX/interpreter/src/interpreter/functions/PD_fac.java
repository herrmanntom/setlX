package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_fac extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fac();

    private PD_fac() {
        super("fac");
        addParameter(new ParameterDef("integer"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).factorial();
    }
}


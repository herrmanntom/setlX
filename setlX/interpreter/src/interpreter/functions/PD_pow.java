package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_pow extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_pow();

    private PD_pow() {
        super("pow");
        addParameter(new ParameterDef("set"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).powerSet();
    }
}


package interpreter.functions;

import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_isBoolean extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isBoolean();

    private PD_isBoolean() {
        super("isBoolean");
        addParameter(new ParameterDef("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isBoolean();
    }
}


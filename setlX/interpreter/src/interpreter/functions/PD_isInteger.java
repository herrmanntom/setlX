package interpreter.functions;

import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_isInteger extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isInteger();

    private PD_isInteger() {
        super("isInteger");
        addParameter(new ParameterDef("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isInteger();
    }
}


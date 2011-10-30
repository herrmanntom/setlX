package interpreter.functions;

import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_str extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_str();

    private PD_str() {
        super("str");
        addParameter(new ParameterDef("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).str();
    }
}

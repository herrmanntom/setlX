package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_isProcedure extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isProcedure();

    private PD_isProcedure() {
        super("isProcedure");
        addParameter(new ParameterDef("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isProcedure();
    }
}


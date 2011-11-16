package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

public class PD_getEnv extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getEnv();

    private PD_getEnv() {
        super("getEnv");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}


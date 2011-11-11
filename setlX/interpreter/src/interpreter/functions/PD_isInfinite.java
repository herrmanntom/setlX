package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

public class PD_isInfinite extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isInfinite();

    private PD_isInfinite() {
        super("isInfinite");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isInfinity();
    }
}


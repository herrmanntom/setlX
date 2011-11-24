package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

public class PD_isProcedure extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isProcedure();

    private PD_isProcedure() {
        super("isProcedure");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isProcedure();
    }
}


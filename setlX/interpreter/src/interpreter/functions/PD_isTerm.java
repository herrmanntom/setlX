package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

public class PD_isTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isTerm();

    private PD_isTerm() {
        super("isTerm");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isTerm();
    }
}


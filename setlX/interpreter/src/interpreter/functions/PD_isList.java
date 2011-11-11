package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

public class PD_isList extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isList();

    private PD_isList() {
        super("isList");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isList();
    }
}


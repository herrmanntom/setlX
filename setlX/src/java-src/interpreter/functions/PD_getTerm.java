package interpreter.functions;

import interpreter.types.Value;

import java.util.List;

// getTerm(value)          : convert a value into a term

public class PD_getTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getTerm();

    private PD_getTerm() {
        super("getTerm");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).toTerm();
    }
}


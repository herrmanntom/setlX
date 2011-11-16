package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

public class PD_getTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getTerm();

    private PD_getTerm() {
        super("getTerm");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}


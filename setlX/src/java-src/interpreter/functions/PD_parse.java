package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

public class PD_parse extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parse();

    private PD_parse() {
        super("parse");
        addParameter("setlX_statements");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}


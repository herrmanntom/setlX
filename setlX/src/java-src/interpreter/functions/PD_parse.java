package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

// parse(expr)             : parse SetlX expression into a term

public class PD_parse extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parse();

    private PD_parse() {
        super("parse");
        addParameter("setlX_expr");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}


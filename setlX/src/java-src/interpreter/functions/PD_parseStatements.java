package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

// parseStatements(stmnts) : parse SetlX statements into a term

public class PD_parseStatements extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parseStatements();

    private PD_parseStatements() {
        super("parseStatements");
        addParameter("setlX_statements");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}


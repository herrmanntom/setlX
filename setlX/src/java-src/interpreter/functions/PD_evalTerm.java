package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

// evalTerm(term)          : execute a term which represents SetlX statements and expessions

public class PD_evalTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_evalTerm();

    private PD_evalTerm() {
        super("evalTerm");
        addParameter("term");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}


package interpreter.functions;

import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

// canonical(term)         : returns a string of a term in its true form

public class PD_canonical extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_canonical();

    private PD_canonical() {
        super("canonical");
        addParameter("term");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return new SetlString(args.get(0).canonical());
    }
}


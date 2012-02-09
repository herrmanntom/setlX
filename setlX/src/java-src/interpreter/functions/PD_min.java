package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

import java.util.List;

// min(collectionValue) : select minumum member from collection value

public class PD_min extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_min();

    private PD_min() {
        super("min");
        addParameter("collectionValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).minimumMember();
    }
}


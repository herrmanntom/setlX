package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

// first(collectionValue)        : selects the first member from `collectionValue'

public class PD_first extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_first();

    private PD_first() {
        super("first");
        addParameter("collectionValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).firstMember();
    }
}


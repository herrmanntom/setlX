package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

// last(collectionValue)         : selects the last member from `collectionValue'

public class PD_last extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_last();

    private PD_last() {
        super("last");
        addParameter("collectionValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).lastMember();
    }
}


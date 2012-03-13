package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

import java.util.List;

// arb(collectionValue)    : selects an arbitrary member from `collectionValue'

public class PD_arb extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_arb();

    private PD_arb() {
        super("arb");
        addParameter("collectionValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).arbitraryMember();
    }
}


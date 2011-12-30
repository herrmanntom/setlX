package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

import java.util.List;

// arb(compoundValue)      : select arbitrary member from compound value

public class PD_arb extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_arb();

    private PD_arb() {
        super("arb");
        addParameter("compoundValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).arbitraryMember();
    }
}


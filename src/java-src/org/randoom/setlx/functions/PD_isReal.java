package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isReal(value)           : test if value-type is real

public class PD_isReal extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isReal();

    private PD_isReal() {
        super("isReal");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isReal();
    }
}


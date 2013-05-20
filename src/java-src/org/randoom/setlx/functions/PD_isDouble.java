package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isReal(value)           : test if value-type is real

public class PD_isDouble extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_isDouble();

    private PD_isDouble() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isDouble();
    }
}


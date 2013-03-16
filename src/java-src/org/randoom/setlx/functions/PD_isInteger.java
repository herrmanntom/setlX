package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isInteger(value)        : test if value-type is integer

public class PD_isInteger extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_isInteger();

    private PD_isInteger() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isInteger();
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isProcedure(value)      : test if value-type is procedure

public class PD_isProcedure extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isProcedure();

    private PD_isProcedure() {
        super("isProcedure");
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isProcedure();
    }
}


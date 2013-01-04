package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isBoolean(value)        : test if value-type is boolean

public class PD_isBoolean extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isBoolean();

    private PD_isBoolean() {
        super("isBoolean");
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isBoolean();
    }
}


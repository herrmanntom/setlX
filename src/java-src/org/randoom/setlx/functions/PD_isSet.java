package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isSet(value)            : test if value-type is set

public class PD_isSet extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isSet();

    private PD_isSet() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isSet();
    }
}


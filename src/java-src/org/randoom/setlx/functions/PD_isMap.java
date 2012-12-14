package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isMap(value)            : test if value-type is map

public class PD_isMap extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isMap();

    private PD_isMap() {
        super("isMap");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isMap();
    }
}


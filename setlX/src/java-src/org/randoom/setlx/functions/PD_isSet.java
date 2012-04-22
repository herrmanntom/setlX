package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;

import java.util.List;

// isSet(value)            : test if value-type is set

public class PD_isSet extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isSet();

    private PD_isSet() {
        super("isSet");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isSet();
    }
}


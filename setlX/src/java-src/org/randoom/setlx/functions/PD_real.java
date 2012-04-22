package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;

import java.util.List;

// real(stringOrNumber)    : convert string or number into a real, returns om on failure

public class PD_real extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_real();

    private PD_real() {
        super("real");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).toReal();
    }
}


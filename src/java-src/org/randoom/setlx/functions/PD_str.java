package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// str(value)              : converts any value into a string

public class PD_str extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_str();

    private PD_str() {
        super("str");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).str();
    }
}


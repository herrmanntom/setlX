package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// int(stringOrNumber)     : convert string or number into an integer, returns om on failure

public class PD_int extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_int();

    private PD_int() {
        super("int");
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).toInteger();
    }
}


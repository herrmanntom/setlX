package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// range(map)              : get range of map

public class PD_range extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_range();

    private PD_range() {
        super("range");
        addParameter("map");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).range(state);
    }
}


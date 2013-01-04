package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// domain(map)             : get domain of map

public class PD_domain extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_domain();

    private PD_domain() {
        super("domain");
        addParameter("map");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).domain();
    }
}


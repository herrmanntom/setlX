package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// str(value)              : converts any value into a string

public class PD_str extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_str();

    private PD_str() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).str(state);
    }
}


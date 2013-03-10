package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// abs(value)              : returns the absolute (e.g. positive) value of the parameter

public class PD_abs extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_abs();

    private PD_abs() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).absoluteValue(state);
    }
}


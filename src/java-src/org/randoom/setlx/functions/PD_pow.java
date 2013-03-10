package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// pow(set)                : computes the power-set

public class PD_pow extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_pow();

    private PD_pow() {
        super();
        addParameter("set");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).powerSet(state);
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// rational(stringOrNumber)      : convert string or number into a rational, returns om on failure

public class PD_rational extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_rational();

    private PD_rational() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).toRational(state);
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * double(stringOrNumber) : Convert string or number into a double, returns om on failure.
 */
public class PD_double extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `double'. */
    public final static PreDefinedProcedure DEFINITION = new PD_double();

    private PD_double() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).toDouble(state);
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * int(stringOrNumber) : Convert string or number into an integer, returns om on failure.
 */
public class PD_int extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `int'. */
    public final static PreDefinedProcedure DEFINITION = new PD_int();

    private PD_int() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).toInteger(state);
    }
}


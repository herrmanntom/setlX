package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * str(value) : Converts any value into a string.
 */
public class PD_str extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `str'. */
    public final static PreDefinedProcedure DEFINITION = new PD_str();

    private PD_str() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).str(state);
    }
}


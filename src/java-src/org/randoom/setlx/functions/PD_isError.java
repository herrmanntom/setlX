package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * isError(value) : Test if value-type is error.
 */
public class PD_isError extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `isError'. */
    public final static PreDefinedProcedure DEFINITION = new PD_isError();

    private PD_isError() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isError();
    }
}


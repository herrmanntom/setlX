package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isError(value) : Test if value-type is error.
 */
public class PD_isError extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isError'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isError();

    private PD_isError() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) {
        return args.get(VALUE).isError();
    }
}


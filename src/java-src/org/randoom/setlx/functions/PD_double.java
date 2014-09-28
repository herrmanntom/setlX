package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * double(stringOrNumber) : Convert string or number into a double, returns om on failure.
 */
public class PD_double extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `double'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_double();

    private PD_double() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(VALUE).toDouble(state);
    }
}


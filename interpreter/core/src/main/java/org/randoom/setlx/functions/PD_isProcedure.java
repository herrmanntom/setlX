package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isProcedure(value) : Test if value-type is procedure.
 */
public class PD_isProcedure extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isProcedure'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isProcedure();

    private PD_isProcedure() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) {
        return args.get(VALUE).isProcedure();
    }
}


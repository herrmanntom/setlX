package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isInfinite(value) : test if value-type is double and the value is INFINITY or -INFINITY
 */
public class PD_isInfinite extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isInfinite'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isInfinite();

    private PD_isInfinite() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return args.get(VALUE).isInfinite();
    }
}


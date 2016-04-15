package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isSet(value) : Test if value-type is set.
 */
public class PD_isSet extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isSet'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isSet();

    private PD_isSet() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return args.get(VALUE).isSet();
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * domain(map) : Get domain of map.
 */
public class PD_domain extends PreDefinedProcedure {

    private final static ParameterDefinition MAP        = createParameter("map");

    /** Definition of the PreDefinedProcedure `domain'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_domain();

    private PD_domain() {
        super();
        addParameter(MAP);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(MAP).domain(state);
    }
}


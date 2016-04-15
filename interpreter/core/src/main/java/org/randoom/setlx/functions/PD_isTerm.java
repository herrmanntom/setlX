package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isTerm(value) : Test if value-type is term.
 */
public class PD_isTerm extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isTerm'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isTerm();

    private PD_isTerm() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return args.get(VALUE).isTerm();
    }
}


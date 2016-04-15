package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isInteger(value) : Test if value-type is integer.
 */
public class PD_isInteger extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isInteger'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isInteger();

    private PD_isInteger() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return args.get(VALUE).isInteger();
    }
}


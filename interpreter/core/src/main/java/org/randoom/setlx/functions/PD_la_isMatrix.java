package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * la_isMatrix(value) : Test if value-type is matrix.
 */
public class PD_la_isMatrix extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `la_isMatrix'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_la_isMatrix();

    private PD_la_isMatrix() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return args.get(VALUE).isMatrix();
    }
}


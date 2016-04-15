package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * ceil(numberValue) : Returns minimum integer which is greater or equal to numberValue.
 */
public class PD_ceil extends PreDefinedProcedure {

    private final static ParameterDefinition NUMBER_VALUE = createParameter("numberValue");

    /** Definition of the PreDefinedProcedure `ceil'. */
    public  final static PreDefinedProcedure DEFINITION   = new PD_ceil();

    private PD_ceil() {
        super();
        addParameter(NUMBER_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(NUMBER_VALUE).ceil(state);
    }
}


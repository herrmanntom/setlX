package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * pow(set) : Computes the power-set.
 */
public class PD_pow extends PreDefinedProcedure {

    private final static ParameterDefinition SET        = createParameter("set");

    /** Definition of the PreDefinedProcedure `pow'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_pow();

    private PD_pow() {
        super();
        addParameter(SET);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(SET).powerSet(state);
    }
}


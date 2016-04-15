package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * resetRandom() : Reseeds the random number generator with 0.
 */
public class PD_resetRandom extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `resetRandom'. */
    public final static PreDefinedProcedure DEFINITION = new PD_resetRandom();

    private PD_resetRandom() {
        super();
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        state.setRandoomPredictable(true);
        return Om.OM;
    }
}


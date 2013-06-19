package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// resetRandom()                 : reseeds the random number generator with 0

public class PD_resetRandom extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_resetRandom();

    private PD_resetRandom() {
        super();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        state.setRandoomPredictable(true);
        return Om.OM;
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * getOsID()               : get identifier for the operating system that setlX runs on
 */
public class PD_getOsID extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `getOsID'. */
    public final static PreDefinedProcedure DEFINITION = new PD_getOsID();

    private PD_getOsID() {
        super();
    }

    @Override
    public Value execute(final State state,
                         final HashMap<ParameterDefinition, Value> args
    ) {
        return new SetlString(state.getOsID());
    }
}


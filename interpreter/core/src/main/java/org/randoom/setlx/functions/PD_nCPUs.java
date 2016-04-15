package org.randoom.setlx.functions;

import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * nCPUs() : Get number of CPUs in current system.
 */
public class PD_nCPUs extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `nCPUs'. */
    public final static PreDefinedProcedure DEFINITION = new PD_nCPUs();

    private PD_nCPUs() {
        super();
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return Rational.valueOf(state.getNumberOfCores());
    }
}


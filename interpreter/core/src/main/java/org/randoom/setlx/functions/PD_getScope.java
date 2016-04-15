package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * getScope() : Get a term representing all variables set in current scope.
 */
public class PD_getScope extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `getScope'. */
    public final static PreDefinedProcedure DEFINITION = new PD_getScope();

    private PD_getScope() {
        super();
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return state.scopeToTerm();
    }
}


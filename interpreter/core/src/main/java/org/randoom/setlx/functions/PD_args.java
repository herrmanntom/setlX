package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * args(term) : get arguments of term
 */
public class PD_args extends PreDefinedProcedure {

    private final static ParameterDefinition TERM       = createParameter("term");

    /** Definition of the PreDefinedProcedure `args'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_args();

    private PD_args() {
        super();
        addParameter(TERM);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(TERM).arguments(state);
    }
}


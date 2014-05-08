package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * args(term) : get arguments of term
 */
public class PD_args extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `args'. */
    public final static PreDefinedProcedure DEFINITION = new PD_args();

    private PD_args() {
        super();
        addParameter("term");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).arguments(state);
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * range(map) : Get range of map.
 */
public class PD_range extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `range'. */
    public final static PreDefinedProcedure DEFINITION = new PD_range();

    private PD_range() {
        super();
        addParameter("map");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).range(state);
    }
}


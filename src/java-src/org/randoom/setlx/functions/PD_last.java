package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * last(collectionValue) : Selects the last member from `collectionValue'.
 */
public class PD_last extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `last'. */
    public final static PreDefinedProcedure DEFINITION = new PD_last();

    private PD_last() {
        super();
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).lastMember(state);
    }
}


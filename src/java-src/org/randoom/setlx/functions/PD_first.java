package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * first(collectionValue) : Selects the first member from `collectionValue'.
 */
public class PD_first extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `first'. */
    public final static PreDefinedProcedure DEFINITION = new PD_first();

    private PD_first() {
        super();
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).firstMember(state);
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// max(collectionValue) : select maximum member from collection value

public class PD_max extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_max();

    private PD_max() {
        super();
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).maximumMember(state);
    }
}


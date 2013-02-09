package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// min(collectionValue) : select minimum member from collection value

public class PD_min extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_min();

    private PD_min() {
        super("min");
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).minimumMember(state);
    }
}


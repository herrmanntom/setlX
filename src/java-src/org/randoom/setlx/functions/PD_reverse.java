package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// reverse(collectionValue)      : reverse the order of members in `collectionValue'

public class PD_reverse extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_reverse();

    private PD_reverse() {
        super();
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).reverse(state);
    }
}


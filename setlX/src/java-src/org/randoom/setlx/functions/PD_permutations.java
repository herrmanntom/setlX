package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// permutations(collectionValue) : computes all permutations of the element in `collectionValue'

public class PD_permutations extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_permutations();

    private PD_permutations() {
        super("permutations");
        addParameter("collectionValue");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).permutations(state);
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// nextPermutation(list)         : returns the next permutation of the list, om if there `list' already is the last permutation

public class PD_nextPermutation extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_nextPermutation();

    private PD_nextPermutation() {
        super("nextPermutation");
        addParameter("list");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws SetlException {

        return args.get(0).nextPermutation(state);

    }

}


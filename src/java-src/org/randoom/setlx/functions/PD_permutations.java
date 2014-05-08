package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * permutations(collectionValue) : Computes all permutations of the element in `collectionValue'.
 */
public class PD_permutations extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `permutations'. */
    public final static PreDefinedProcedure DEFINITION = new PD_permutations();

    private PD_permutations() {
        super();
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).permutations(state);
    }
}


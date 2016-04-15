package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * permutations(collectionValue) : Computes all permutations of the element in `collectionValue'.
 */
public class PD_permutations extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `permutations'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_permutations();

    private PD_permutations() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).permutations(state);
    }
}


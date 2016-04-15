package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * last(collectionValue) : Selects the last member from `collectionValue'.
 */
public class PD_last extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `last'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_last();

    private PD_last() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).lastMember(state);
    }
}


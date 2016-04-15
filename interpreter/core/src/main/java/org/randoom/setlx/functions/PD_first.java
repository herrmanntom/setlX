package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * first(collectionValue) : Selects the first member from `collectionValue'.
 */
public class PD_first extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `first'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_first();
    
    private PD_first() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).firstMember(state);
    }
}


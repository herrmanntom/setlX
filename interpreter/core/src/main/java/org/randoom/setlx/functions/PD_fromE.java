package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * fromE(rw collectionValue)     : selects and removes the last member from `collectionValue'
 */
public class PD_fromE extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createRwParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `fromE'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_fromE();

    private PD_fromE() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value collection = args.get(COLLECTION_VALUE);
        final Value element    = collection.removeLastMember(state);

        // write the reduced collection back into the outer environment
        args.put(COLLECTION_VALUE, collection);

        return element;
    }
}


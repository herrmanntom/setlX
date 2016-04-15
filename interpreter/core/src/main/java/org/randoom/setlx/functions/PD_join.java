package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * join(collection, separator) : Returns a string with all the members in `collection' separated by `separator'.
 */
public class PD_join extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");
    private final static ParameterDefinition SEPARATOR        = createParameter("separator");

    /** Definition of the PreDefinedProcedure `join'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_join();

    private PD_join() {
        super();
        addParameter(COLLECTION_VALUE);
        addParameter(SEPARATOR);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {

        return args.get(COLLECTION_VALUE).join(state, args.get(SEPARATOR));

    }

}


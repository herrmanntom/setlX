package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * reverse(collectionValue) : Reverse the order of members in `collectionValue'.
 */
public class PD_reverse extends PreDefinedProcedure {

    private final static ParameterDef        COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `reverse'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_reverse();

    private PD_reverse() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).reverse(state);
    }
}

